package com.stagelog.Stagelog.migration.service;

import com.stagelog.Stagelog.domain.KopisPerformance;
import com.stagelog.Stagelog.migration.domain.RefinedPerformanceV2;
import com.stagelog.Stagelog.migration.dto.FilterResult;
import com.stagelog.Stagelog.migration.repository.RefinedPerformanceV2Repository;
import com.stagelog.Stagelog.repository.KopisPerformanceRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 화이트리스트 기반 정제 프로세스 테스트 서비스
 * RefinedPerformanceV2 테이블에 저장하여 기존 데이터에 영향 없음
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestRefinementService {

    private final KopisPerformanceRepository kopisRepository;
    private final RefinedPerformanceV2Repository refinedV2Repository;
    private final KopisWhitelistFilterService whitelistFilterService;

    /**
     * 화이트리스트 필터링 테스트 (실제 저장 없음)
     *
     * @return 필터링 결과
     */
    @Transactional(readOnly = true)
    public FilterResult testWhitelistFiltering() {

        log.info("=== 화이트리스트 필터링 테스트 시작 ===");

        // 1. 상세 정보가 있는 공연 조회 (기존 정제 여부 무시)
        List<KopisPerformance> rawList = kopisRepository.findByHasDetailTrue();
        log.info("테스트 대상 공연 수: {}개", rawList.size());

        if (rawList.isEmpty()) {
            log.warn("테스트할 공연 데이터가 없습니다.");
            return FilterResult.empty();
        }

        // 2. 화이트리스트 필터링 적용
        FilterResult result = whitelistFilterService.filterByWhitelist(rawList);

        // 3. 결과 로깅
        log.info("=== 필터링 결과 ===");
        log.info("전체: {}개", result.getTotalCount());
        log.info("선택: {}개 ({}%)", result.getSelectedCount(), String.format("%.2f", result.getSelectRate()));
        log.info("제외: {}개 ({}%)", result.getExcludedCount(), String.format("%.2f", 100 - result.getSelectRate()));

        // 4. 선택된 공연 샘플 출력
        log.info("=== 선택된 공연 샘플 (최대 10개) ===");
        result.getSelectedPerformances().stream()
            .limit(10)
            .forEach(p -> log.info("  - [{}] {}", p.getKopisId(), p.getTitle()));

        return result;
    }

    /**
     * 실제 정제 프로세스 실행 (RefinedPerformanceV2에 저장)
     *
     * @param dryRun true면 실제 저장하지 않고 로깅만, false면 RefinedPerformanceV2에 저장
     * @return 정제 결과
     */
    @Transactional
    public RefinementResult executeRefinement(boolean dryRun) {

        log.info("=== 화이트리스트 기반 정제 프로세스 시작 (dryRun={}) ===", dryRun);

        // 1. 대상 데이터 조회 (상세 정보가 있는 모든 공연)
        List<KopisPerformance> rawList = kopisRepository.findByHasDetailTrue();
        log.info("정제 대상: {}개", rawList.size());

        if (rawList.isEmpty()) {
            log.info("정제할 공연 데이터가 없습니다.");
            return RefinementResult.empty();
        }

        // 2. 화이트리스트 필터링
        FilterResult filterResult = whitelistFilterService.filterByWhitelist(rawList);
        List<KopisPerformance> targetPerformances = filterResult.getSelectedPerformances();

        if (targetPerformances.isEmpty()) {
            log.info("화이트리스트에 매칭되는 공연이 없습니다.");
            return RefinementResult.fromFilter(filterResult);
        }

        log.info("필터링 후: {}개 공연 선택됨", targetPerformances.size());

        // 3. 매칭 정보 수집 (어떤 아티스트가 어떤 키워드로 매칭했는지)
        Map<String, KopisWhitelistFilterService.MatchInfo> matchInfoMap = collectMatchInfo(targetPerformances);

        // 4. 정제 처리
        int successCount = 0;
        int failCount = 0;
        int updatedCount = 0;
        int newCount = 0;

        for (KopisPerformance raw : targetPerformances) {
            try {
                if (!dryRun) {
                    // RefinedPerformanceV2로 변환
                    KopisWhitelistFilterService.MatchInfo matchInfo = matchInfoMap.get(raw.getKopisId());

                    // Optional 처리를 if-else로 변경 (람다 변수 문제 해결)
                    var existing = refinedV2Repository.findByKopisId(raw.getKopisId());
                    if (existing.isPresent()) {
                        // 기존 데이터 업데이트
                        updateRefinedV2(existing.get(), raw, matchInfo);
                        updatedCount++;
                    } else {
                        // 새로운 데이터 저장
                        RefinedPerformanceV2 refined = convertToRefinedV2(raw, matchInfo);
                        refinedV2Repository.save(refined);
                        newCount++;
                    }
                }

                successCount++;
                log.debug("정제 성공: [{}] {}", raw.getKopisId(), raw.getTitle());

            } catch (Exception e) {
                failCount++;
                log.error("정제 실패 - KopisID: {}, 사유: {}", raw.getKopisId(), e.getMessage(), e);
            }
        }

        log.info("=== 정제 프로세스 완료 ===");
        log.info("성공: {}개 (신규: {}개, 업데이트: {}개), 실패: {}개",
                 successCount, newCount, updatedCount, failCount);

        return RefinementResult.builder()
            .filterResult(filterResult)
            .refinedCount(successCount)
            .failedCount(failCount)
            .newCount(newCount)
            .updatedCount(updatedCount)
            .dryRun(dryRun)
            .build();
    }

    /**
     * 각 공연의 매칭 정보 수집 (어떤 아티스트, 어떤 키워드로 매칭되었는지)
     */
    private Map<String, KopisWhitelistFilterService.MatchInfo> collectMatchInfo(List<KopisPerformance> performances) {
        Map<String, KopisWhitelistFilterService.MatchInfo> matchInfoMap = new HashMap<>();

        for (KopisPerformance performance : performances) {
            KopisWhitelistFilterService.MatchInfo matchInfo = whitelistFilterService.getMatchInfo(performance);
            if (matchInfo != null) {
                matchInfoMap.put(performance.getKopisId(), matchInfo);
            }
        }

        return matchInfoMap;
    }

    /**
     * KopisPerformance를 RefinedPerformanceV2로 변환
     */
    private RefinedPerformanceV2 convertToRefinedV2(KopisPerformance raw, KopisWhitelistFilterService.MatchInfo matchInfo) {
        RefinedPerformanceV2 refined = RefinedPerformanceV2.builder()
            .kopisId(raw.getKopisId())
            .title(raw.getTitle())
            .posterUrl(raw.getPosterUrl())
            .venue(raw.getVenue())
            .status(raw.getStatus())
            .startDate(raw.getStartDate())
            .endDate(raw.getEndDate())
            .genre(raw.getGenre())
            .hasDetail(raw.getHasDetail())
            .cast(raw.getCast())
            .runtime(raw.getRuntime())
            .ticketPrice(raw.getTicketPrice())
            .area(raw.getArea())
            .performanceStartTime(raw.getPerformanceStartTime())
            .isVisit(raw.isVisit())
            .isFestival(raw.isFestival())
            .ticketVendor(raw.getTicketVendor())
            .ticketUrl(raw.getTicketUrl())
            .build();

        // 매칭 정보 설정
        if (matchInfo != null) {
            refined.setMatchingInfo(matchInfo.getMatchedArtist(), matchInfo.getMatchedKeyword());
        }

        return refined;
    }

    /**
     * 기존 RefinedPerformanceV2 업데이트
     */
    private void updateRefinedV2(RefinedPerformanceV2 existing, KopisPerformance raw, KopisWhitelistFilterService.MatchInfo matchInfo) {
        // KOPIS 데이터 동기화
        existing.syncWithKopisData(
            raw.getTitle(), raw.getPosterUrl(), raw.getVenue(), raw.getStatus(),
            raw.getStartDate(), raw.getEndDate(), raw.getGenre(), raw.getHasDetail(),
            raw.getCast(), raw.getRuntime(), raw.getTicketPrice(), raw.getArea(),
            raw.getPerformanceStartTime(), raw.isVisit(), raw.isFestival(),
            raw.getTicketVendor(), raw.getTicketUrl()
        );

        // 매칭 정보 업데이트
        if (matchInfo != null) {
            existing.setMatchingInfo(matchInfo.getMatchedArtist(), matchInfo.getMatchedKeyword());
        }
    }

    /**
     * 정제 통계 조회
     */
    @Transactional(readOnly = true)
    public RefinementStatistics getStatistics() {
        long totalCount = refinedV2Repository.count();
        List<Object[]> keywordStats = refinedV2Repository.getMatchedKeywordStatistics();

        return RefinementStatistics.builder()
            .totalRefinedCount(totalCount)
            .keywordStatistics(keywordStats)
            .build();
    }

    /**
     * 테스트 데이터 전체 삭제
     */
    @Transactional
    public void deleteAllTestData() {
        refinedV2Repository.deleteAll();
        log.info("모든 RefinedPerformanceV2 테스트 데이터 삭제 완료");
    }

    // ============================================================
    // 내부 DTO
    // ============================================================

    @Getter
    @Builder
    public static class RefinementResult {
        private FilterResult filterResult;
        private int refinedCount;
        private int failedCount;
        private int newCount;
        private int updatedCount;
        private boolean dryRun;

        public static RefinementResult empty() {
            return RefinementResult.builder()
                .filterResult(FilterResult.empty())
                .refinedCount(0)
                .failedCount(0)
                .newCount(0)
                .updatedCount(0)
                .dryRun(true)
                .build();
        }

        public static RefinementResult fromFilter(FilterResult filterResult) {
            return RefinementResult.builder()
                .filterResult(filterResult)
                .refinedCount(0)
                .failedCount(0)
                .newCount(0)
                .updatedCount(0)
                .dryRun(true)
                .build();
        }
    }

    @Getter
    @Builder
    public static class RefinementStatistics {
        private long totalRefinedCount;
        private List<Object[]> keywordStatistics;
    }
}
