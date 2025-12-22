package com.stagelog.Stagelog.dto;

import com.stagelog.Stagelog.domain.RefinedPerformance;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PerformanceCalenderResponse {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    public static PerformanceCalenderResponse from(RefinedPerformance refinedPerformance) {
        return new PerformanceCalenderResponse(
                refinedPerformance.getId(),
                refinedPerformance.getTitle(),
                refinedPerformance.getStartDate(),
                refinedPerformance.getEndDate()
        );

    }
}
