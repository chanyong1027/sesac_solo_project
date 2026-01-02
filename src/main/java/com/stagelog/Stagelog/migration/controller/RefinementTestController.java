package com.stagelog.Stagelog.migration.controller;

import com.stagelog.Stagelog.migration.dto.FilterResult;
import com.stagelog.Stagelog.migration.service.TestRefinementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/migration/refinement")
@RequiredArgsConstructor
@Slf4j
public class RefinementTestController {

    private final TestRefinementService testRefinementService;

    /**
     * 화이트리스트 필터링 테스트 (저장 없음)
     */
    @GetMapping("/test-filter")
    public ResponseEntity<FilterResult> testWhitelistFiltering() {
        log.info("화이트리스트 필터링 테스트 요청");
        FilterResult result = testRefinementService.testWhitelistFiltering();
        return ResponseEntity.ok(result);
    }

    /**
     * 정제 프로세스 실행 (Dry Run)
     */
    @PostMapping("/execute/dry-run")
    public ResponseEntity<TestRefinementService.RefinementResult> executeDryRun() {
        log.info("정제 프로세스 Dry Run 요청");
        TestRefinementService.RefinementResult result = testRefinementService.executeRefinement(true);
        return ResponseEntity.ok(result);
    }

    /**
     * 정제 프로세스 실행 (실제 저장)
     */
    @PostMapping("/execute")
    public ResponseEntity<TestRefinementService.RefinementResult> executeRefinement() {
        log.info("정제 프로세스 실행 요청");
        TestRefinementService.RefinementResult result = testRefinementService.executeRefinement(false);
        return ResponseEntity.ok(result);
    }

    /**
     * 정제 통계 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<TestRefinementService.RefinementStatistics> getStatistics() {
        log.info("정제 통계 조회 요청");
        TestRefinementService.RefinementStatistics statistics = testRefinementService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 테스트 데이터 전체 삭제
     */
    @DeleteMapping("/test-data")
    public ResponseEntity<Void> deleteAllTestData() {
        log.info("테스트 데이터 전체 삭제 요청");
        testRefinementService.deleteAllTestData();
        return ResponseEntity.ok().build();
    }
}
