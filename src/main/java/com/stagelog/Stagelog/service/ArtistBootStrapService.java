package com.stagelog.Stagelog.service;

import com.stagelog.Stagelog.domain.Artist;
import com.stagelog.Stagelog.domain.KopisPerformance;
import com.stagelog.Stagelog.repository.ArtistRepository;
import com.stagelog.Stagelog.repository.KopisPerformanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class
ArtistBootStrapService {
    private final KopisPerformanceRepository kopisPerformanceRepository;
    private final KopisFilterService kopisFilterService;
    private final ArtistNameExtractor extractor;
    private final ArtistRepository artistRepository;

    @Transactional
    public BootstrapResult execute(){
        log.info("========== Artist 부트스트랩 시작 ==========");

        List<KopisPerformance> allPerformances = kopisPerformanceRepository.findAll();
        log.info("Step 1: KOPIS 데이터 로드 완료 - {}개", allPerformances.size());

        List<KopisPerformance> filteredPerformances = kopisFilterService.filterOutNonTarget(allPerformances);
        log.info("Step 2: 사전 필터링 완료 - {}개 남음", filteredPerformances.size());

        // 3. 아티스트명 추출
        ExtractionResult extractionResult = extractArtistNames(filteredPerformances);
        log.info("Step 3: 아티스트명 추출 완료 - {}개 고유 이름",
                extractionResult.getArtistSources().size());

        // 4. Artist 저장
        SaveResult saveResult = saveArtists(extractionResult.getArtistSources());
        log.info("Step 4: Artist 저장 완료 - {}개 신규 저장", saveResult.getSavedCount());

        // 5. 결과 집계
        BootstrapResult result = BootstrapResult.builder()
                .originalCount(allPerformances.size())
                .filteredCount(filteredPerformances.size())
                .excludedCount(allPerformances.size() - filteredPerformances.size())
                .festivalCount(extractionResult.getFestivalCount())
                .extractedCount(extractionResult.getExtractedCount())
                .failedCount(extractionResult.getFailedCount())
                .uniqueArtistCount(extractionResult.getArtistSources().size())
                .savedCount(saveResult.getSavedCount())
                .duplicateCount(saveResult.getDuplicateCount())
                .build();

        log.info("========================================");
        log.info("Artist 부트스트랩 완료");
        log.info("========================================");
        log.info("원본 공연: {}개", result.getOriginalCount());

        log.info("사전 제외: {}개 ({}%)",
                result.getExcludedCount(),
                String.format("%.1f", result.getExcludedCount() * 100.0 / result.getOriginalCount())
        );
        log.info("필터링 후: {}개", result.getFilteredCount());
        log.info("");
        log.info("페스티벌: {}개 (추출 스킵)", result.getFestivalCount());
        log.info("추출 성공: {}개", result.getExtractedCount());
        log.info("추출 실패: {}개", result.getFailedCount());
        log.info("고유 아티스트: {}개", result.getUniqueArtistCount());
        log.info("");
        log.info("신규 저장: {}개", result.getSavedCount());
        log.info("중복 스킵: {}개", result.getDuplicateCount());
        log.info("========================================");

        return result;
    }


    /**
     * 아티스트명 추출
     */
    private ExtractionResult extractArtistNames(List<KopisPerformance> performances) {

        Map<String, SourceInfo> artistSources = new HashMap<>();

        int festivalCount = 0;
        int extractedCount = 0;
        int failedCount = 0;

        for (KopisPerformance kopis : performances) {

            List<String> names = extractor.extract(kopis.getTitle());

            if (names.isEmpty()) {
                // 페스티벌이거나 추출 실패
                if (isFestival(kopis.getTitle())) {
                    festivalCount++;
                } else {
                    failedCount++;
                    log.debug("추출 실패: {}", kopis.getTitle());
                }
            } else {
                extractedCount++;

                for (String name : names) {
                    // 이미 있으면 스킵 (첫 번째 소스만 저장)
                    if (!artistSources.containsKey(name)) {
                        artistSources.put(name, SourceInfo.builder()
                                .artistName(name)
                                .performanceTitle(kopis.getTitle())
                                .kopisId(kopis.getKopisId())
                                .build());
                    }
                }
                if (log.isTraceEnabled()) {
                    log.trace("추출 성공: {} → {}", kopis.getTitle(), names);
                }
            }
        }

        return ExtractionResult.builder()
                .artistSources(artistSources)
                .festivalCount(festivalCount)
                .extractedCount(extractedCount)
                .failedCount(failedCount)
                .build();
    }

    /**
     * Artist 저장
     */
    private SaveResult saveArtists(Map<String, SourceInfo> artistSources) {

        int savedCount = 0;
        int duplicateCount = 0;

        for (Map.Entry<String, SourceInfo> entry : artistSources.entrySet()) {

            String name = entry.getKey();
            SourceInfo source = entry.getValue();

            // 중복 체크
            if (artistRepository.existsByName(name)) {
                duplicateCount++;
                log.debug("중복 스킵: {}", name);
                continue;
            }

            // 저장
            Artist artist = new Artist();
            artist.setName(name);
            artist.setSourceTitle(source.getPerformanceTitle());
            artist.setSourceKopisId(source.getKopisId());
            // 나머지 필드는 NULL로 남겨둠 (나중에 Spotify에서 채울 예정)

            artistRepository.save(artist);
            savedCount++;

            log.debug("저장: {} (from: {})", name, source.getPerformanceTitle());
        }

        return SaveResult.builder()
                .savedCount(savedCount)
                .duplicateCount(duplicateCount)
                .build();
    }

    /**
     * 페스티벌 여부 체크
     */
    private boolean isFestival(String title) {
        return title.matches("(?i).*?(페스티벌|페스타|festival|festa).*");
    }

    // ========== 내부 클래스 ==========

    /**
     * 추출 결과
     */
    @lombok.Builder
    @lombok.Getter
    private static class ExtractionResult {
        private final Map<String, SourceInfo> artistSources;
        private final int festivalCount;
        private final int extractedCount;
        private final int failedCount;
    }

        /**
         * 소스 정보
         */
        @lombok.Builder
        @lombok.Getter
        private static class SourceInfo {
            private final String artistName;
            private final String performanceTitle;
            private final String kopisId;
        }

    /**
     * 저장 결과
     */
    @lombok.Builder
    @lombok.Getter
    private static class SaveResult {
        private final int savedCount;
        private final int duplicateCount;
    }

    /**
     * 부트스트랩 최종 결과
     */
    @lombok.Builder
    @lombok.Getter
    public static class BootstrapResult {
        private final int originalCount;      // 원본 공연 수
        private final int filteredCount;      // 필터링 후 공연 수
        private final int excludedCount;      // 사전 제외 수
        private final int festivalCount;      // 페스티벌 수
        private final int extractedCount;     // 추출 성공 수
        private final int failedCount;        // 추출 실패 수
        private final int uniqueArtistCount;  // 고유 아티스트 수
        private final int savedCount;         // 신규 저장 수
        private final int duplicateCount;     // 중복 스킵 수
    }
}
