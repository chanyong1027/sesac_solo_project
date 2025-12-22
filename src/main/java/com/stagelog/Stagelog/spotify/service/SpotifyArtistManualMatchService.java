package com.stagelog.Stagelog.spotify.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stagelog.Stagelog.domain.RefinedPerformance;
import com.stagelog.Stagelog.repository.RefinedPerformanceRepository;
import com.stagelog.Stagelog.spotify.client.SpotifyApiClient;
import com.stagelog.Stagelog.spotify.dto.SpotifyArtistSearchResponse;
import com.stagelog.Stagelog.spotify.util.SpotifyRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyArtistManualMatchService {
    private final RefinedPerformanceRepository refinedPerformanceRepository;
    private final SpotifyApiClient spotifyClient;
    private final SpotifyRateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    @Transactional
    public MatchResult searchAndMatchArtist(){

        List<RefinedPerformance> performances = refinedPerformanceRepository.findByArtistNameKr();
        log.info("대상 공연: {}개", performances.size());

        int success = 0;
        int failed = 0;

        for (int i = 0; i < performances.size(); i++) {
            RefinedPerformance performance = performances.get(i);

            try {
                rateLimiter.acquire();

                if (searchAndUpdateFromSpotify(performance)) {
                    success++;
                } else {
                    failed++;
                }
                refinedPerformanceRepository.save(performance);

            } catch (Exception e) {
                log.error("처리 중 에러 ({}): {}", performance.getTitle(), e.getMessage());
                failed++;
            }
        }
        log.info("=== 완료 ===");
        double successRate = success * 100.0 / performances.size();

        log.info("성공: {}, 실패: {}, 성공률: {}%",
                success,
                failed,
                String.format("%.1f", successRate)
        );
        return new MatchResult(performances.size(), success, failed);
    }

    private boolean searchAndUpdateFromSpotify(RefinedPerformance performance){
        try {
            SpotifyArtistSearchResponse response =
                    spotifyClient.searchArtist(performance.getArtistNameKr());

            if (response == null ||
                    response.getArtists() == null ||
                    response.getArtists().getItems() == null ||
                    response.getArtists().getItems().isEmpty()) {
                return false;
            }

            var spotifyArtist = response.getArtists().getItems().get(0);

            // RefinedPerformance에 직접 저장
            performance.setArtistNameEn(spotifyArtist.getName());

            // 장르 저장
            if (spotifyArtist.getGenres() != null && !spotifyArtist.getGenres().isEmpty()) {
                try {
                    String genres = objectMapper.writeValueAsString(spotifyArtist.getGenres());
                    performance.setArtistGenres(genres);
                } catch (JsonProcessingException e) {
                    log.warn("장르 JSON 변환 실패: {}", performance.getArtistNameKr());
                }
            }

            return true;

        } catch (SpotifyApiClient.SpotifyRateLimitException e) {
            // Rate Limit 대기
            rateLimiter.waitForRetry(e.getRetryAfterSeconds());
            return searchAndUpdateFromSpotify(performance); // 재시도

        } catch (Exception e) {
            log.error("Spotify 검색 실패 ({}): {}", performance.getArtistNameKr(), e.getMessage());
            return false;
        }
    }

    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class MatchResult {
        private final int total;
        private final int success;
        private final int failed;
    }
}
