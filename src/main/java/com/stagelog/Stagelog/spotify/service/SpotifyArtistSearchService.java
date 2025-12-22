package com.stagelog.Stagelog.spotify.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stagelog.Stagelog.domain.Artist;
import com.stagelog.Stagelog.repository.ArtistRepository;
import com.stagelog.Stagelog.spotify.client.SpotifyApiClient;
import com.stagelog.Stagelog.spotify.dto.SpotifyArtistSearchResponse;
import com.stagelog.Stagelog.spotify.util.SpotifyRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyArtistSearchService {

    private final ArtistRepository artistRepository;
    private final SpotifyApiClient spotifyClient;
    private final SpotifyRateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    /**
     * 전체 아티스트 검색 (Phase 1)
     *
     * @param batchSize 배치 크기 (null이면 전체)
     * @return 검색 결과
     */
    @Transactional
    public SearchResult searchAllArtists(Integer batchSize) {

        log.info("========================================");
        log.info("Spotify 아티스트 검색 시작 (Phase 1)");
        log.info("========================================");

        // 아직 검색 안 한 아티스트들
        List<Artist> artists = batchSize != null
                ? artistRepository.findBySearchStatusIsNullLimit(batchSize)
                : artistRepository.findBySearchStatusIsNull();

        log.info("검색 대상: {}개", artists.size());

        int foundCount = 0;
        int notFoundCount = 0;
        int errorCount = 0;

        for (int i = 0; i < artists.size(); i++) {
            Artist artist = artists.get(i);

            try {
                // Rate Limit
                rateLimiter.acquire();

                // 검색
                boolean found = searchAndUpdate(artist);

                if (found) {
                    foundCount++;
                } else {
                    notFoundCount++;
                }

                // 진행률 로그 (100개마다)
                if ((i + 1) % 100 == 0) {
                    double progress = (i + 1) * 100.0 / artists.size();
                    log.info("진행: {}/{} ({}%) | 성공: {}, 실패: {}, 에러: {}",
                            i + 1,
                            artists.size(),
                            String.format("%.1f", progress), // 여기서 포맷팅
                            foundCount,
                            notFoundCount,
                            errorCount
                    );
                }

            } catch (SpotifyApiClient.SpotifyRateLimitException e) {
                // 429 에러 - 대기 후 재시도
                rateLimiter.waitForRetry(e.getRetryAfterSeconds());
                i--;  // 현재 아티스트 재시도

            } catch (Exception e) {
                log.error("검색 중 에러 ({}): {}", artist.getName(), e.getMessage());
                artist.setSearchStatus("ERROR");
                artist.setSearchError(e.getMessage());
                artist.setLastSearchedAt(LocalDateTime.now());
                artistRepository.save(artist);
                errorCount++;
            }
        }

        SearchResult result = SearchResult.builder()
                .totalCount(artists.size())
                .foundCount(foundCount)
                .notFoundCount(notFoundCount)
                .errorCount(errorCount)
                .build();

        log.info("========================================");
        log.info("Spotify 검색 완료");
        log.info("========================================");
        log.info("총 검색: {}개", result.getTotalCount());
        log.info("성공: {}개 ({}%)",
                result.getFoundCount(),
                String.format("%.1f", result.getFoundCount() * 100.0 / result.getTotalCount())
        );

        log.info("실패: {}개 ({}%)",
                result.getNotFoundCount(),
                String.format("%.1f", result.getNotFoundCount() * 100.0 / result.getTotalCount())
        );
        log.info("에러: {}개", result.getErrorCount());
        log.info("========================================");

        return result;
    }

    /**
     * 아티스트 검색 및 업데이트
     *
     * @return 찾았으면 true
     */
    private boolean searchAndUpdate(Artist artist) {

        log.debug("검색: {}", artist.getName());

        // Spotify 검색
        SpotifyArtistSearchResponse response = spotifyClient.searchArtist(artist.getName());

        // 검색 시도 횟수 증가
        int currentAttempts = (artist.getSearchAttempts() == null) ? 0 : artist.getSearchAttempts();
        artist.setSearchAttempts(currentAttempts + 1);
        artist.setLastSearchedAt(LocalDateTime.now());

        // 결과 없음
        if (response == null ||
                response.getArtists() == null ||
                response.getArtists().getItems() == null ||
                response.getArtists().getItems().isEmpty()) {

            artist.setSearchStatus("NOT_FOUND");
            artistRepository.save(artist);

            log.debug("  → 못 찾음");
            return false;
        }

        // 첫 번째 결과 가져오기
        var spotifyArtist = response.getArtists().getItems().get(0);

        String dbName = artist.getName();
        String spotifyName = spotifyArtist.getName();

        boolean isDbKorean = containsKorean(dbName);
        boolean isSpotifyKorean = containsKorean(spotifyName);

        // 둘 중 하나만 한글인 경우 (언어가 다름) -> 유사도 체크 없이 저장
        if (isDbKorean != isSpotifyKorean) {
            log.info("  → 언어 다름 (유사도 스킵): {} (DB) - {} (Spotify)", dbName, spotifyName);

            updateArtistFromSpotify(artist, spotifyArtist);
            artist.setSearchStatus("FOUND");
            artistRepository.save(artist);
            return true;
        }

        // 이름 유사도 체크 (90% 이상)
        double similarity = calculateSimilarity(
                artist.getName().toLowerCase(),
                spotifyArtist.getName().toLowerCase()
        );

        if (similarity < 0.6) {
            // 유사도 낮음 - 재시도 필요
            artist.setSearchStatus("NEED_RETRY");
            artist.setSearchError("Low similarity: " + similarity);
            artistRepository.save(artist);

            log.debug("  → 유사도 낮음 ({}): {}",
                    String.format("%.2f", similarity),
                    spotifyArtist.getName()
            );
            return false;
        }

        // 업데이트
        updateArtistFromSpotify(artist, spotifyArtist);
        artist.setSearchStatus("FOUND");
        artistRepository.save(artist);

        log.debug("  → 찾음: {} ({})", spotifyArtist.getName(), spotifyArtist.getId());
        return true;
    }

    /**
     * Spotify 데이터로 Artist 업데이트
     */
    private void updateArtistFromSpotify(Artist artist, SpotifyArtistSearchResponse.SpotifyArtist spotify) {

        artist.setSpotifyId(spotify.getId());
        artist.setNameEn(spotify.getName());
        artist.setSpotifyPopularity(spotify.getPopularity());

        // 장르 (JSON 문자열로 저장)
        if (spotify.getGenres() != null && !spotify.getGenres().isEmpty()) {
            try {
                artist.setSpotifyGenres(objectMapper.writeValueAsString(spotify.getGenres()));
            } catch (JsonProcessingException e) {
                log.warn("장르 JSON 변환 실패: {}", artist.getName());
            }
        }

        // 이미지
        if (spotify.getImages() != null && !spotify.getImages().isEmpty()) {
            artist.setSpotifyImageUrl(spotify.getImages().get(0).getUrl());
        }
    }

    private boolean containsKorean(String text) {
        if (text == null) return false;
        return text.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }

    /**
     * 문자열 유사도 계산 (Levenshtein Distance)
     */
    private double calculateSimilarity(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;

        int distance = levenshteinDistance(s1, s2);
        return 1.0 - ((double) distance / maxLen);
    }

    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * 검색 결과
     */
    @lombok.Builder
    @lombok.Getter
    public static class SearchResult {
        private final int totalCount;
        private final int foundCount;
        private final int notFoundCount;
        private final int errorCount;
    }
}
