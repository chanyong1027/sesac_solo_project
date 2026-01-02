package com.stagelog.Stagelog.service;

import com.stagelog.Stagelog.domain.RefinedPerformance;
import com.stagelog.Stagelog.dto.PerformanceCalenderResponse;
import com.stagelog.Stagelog.dto.PerformanceDetailResponse;
import com.stagelog.Stagelog.dto.PerformanceListResponse;
import com.stagelog.Stagelog.global.exception.EntityNotFoundException;
import com.stagelog.Stagelog.global.exception.ErrorCode;
import com.stagelog.Stagelog.repository.RefinedPerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefinedPerformanceService {
    private final RefinedPerformanceRepository performanceRepository;

    @Transactional(readOnly = true)
    public Page<PerformanceListResponse> getPerformanceList(Boolean isFestival, String keyword, Pageable pageable) {
        Page<RefinedPerformance> performancePage;

        if (keyword != null && !keyword.isBlank()) {
            performancePage = performanceRepository.searchByKeyword(keyword, pageable);
        }
        // 2. 검색어가 없는 경우 (리스트 조회)
        else {
            if (isFestival != null) {
                // true(페스티벌) 또는 false(국내공연)로 필터링
                performancePage = performanceRepository.findByIsFestival(isFestival, pageable);
            } else {
                // null이면 전체 조회 (혹시 나중에 전체보기 페이지가 필요할 때 대비)
                performancePage = performanceRepository.findAll(pageable);
            }
        }

        return performancePage.map(PerformanceListResponse::from);
    }

    @Transactional(readOnly = true)
    public List<PerformanceCalenderResponse> getMonthlyPerformances(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<RefinedPerformance> performanceList = performanceRepository.findPerformancesInMonth(startOfMonth, endOfMonth);

        return performanceList.stream()
                .map(PerformanceCalenderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PerformanceDetailResponse getPerformanceDetail(Long performanceId) {
        RefinedPerformance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFORMANCE_NOT_FOUND));
        return PerformanceDetailResponse.from(performance);
    }
}
