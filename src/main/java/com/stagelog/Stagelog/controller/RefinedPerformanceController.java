package com.stagelog.Stagelog.controller;

import com.stagelog.Stagelog.dto.PerformanceCalenderResponse;
import com.stagelog.Stagelog.dto.PerformanceDetailResponse;
import com.stagelog.Stagelog.dto.PerformanceListResponse;
import com.stagelog.Stagelog.service.RefinedPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class RefinedPerformanceController {
    private final RefinedPerformanceService performanceService;

    @GetMapping
    public ResponseEntity<Page<PerformanceListResponse>> getPerformances(
            @RequestParam(required = false) Boolean isFestival,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(performanceService.getPerformanceList(isFestival, keyword, pageable));
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<PerformanceCalenderResponse>> getCalendar(
            @RequestParam int year,
            @RequestParam int month
    ){
        return ResponseEntity.ok(performanceService.getMonthlyPerformances(year, month));
    }

    @GetMapping("/{performanceId}")
    public ResponseEntity<PerformanceDetailResponse>  getPerformanceDetail(
            @PathVariable Long performanceId
    ){
        return ResponseEntity.ok(performanceService.getPerformanceDetail(performanceId));

    }
}
