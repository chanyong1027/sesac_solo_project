package com.stagelog.Stagelog.dto;

import com.stagelog.Stagelog.domain.RefinedPerformance;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PerformanceListResponse {
    private Long id;
    private String title;
    private String postUrl;
    private LocalDate startDate;
    private LocalDate endDate;

    public static PerformanceListResponse from(RefinedPerformance refinedPerformance) {
        return new PerformanceListResponse(
                refinedPerformance.getId(),
                refinedPerformance.getTitle(),
                refinedPerformance.getPosterUrl(),
                refinedPerformance.getStartDate(),
                refinedPerformance.getEndDate()
        );
    }
}
