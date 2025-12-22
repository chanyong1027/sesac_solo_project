package com.stagelog.Stagelog.dto;

import com.stagelog.Stagelog.domain.RefinedPerformance;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PerformanceDetailResponse {
    private Long id;
    private String title;
    private String postUrl;
    private List<String> cast;
    private LocalDate startDate;
    private LocalDate endDate;
    private String runtime;
    private String dtguidance;
    private String place;
    private String ticketPrice;
    private String ticketVendor;
    private String ticketUrl;

    public static PerformanceDetailResponse from(RefinedPerformance performance){
        List<String> castList = new ArrayList<>();
        if (performance.getArtistNameEn() != null) {
            castList.add(performance.getArtistNameEn());
        }
        if (performance.getArtistNameKr() != null) {
            castList.add(performance.getArtistNameKr());
        }

        if (castList.isEmpty()) {
            castList.add("출연진 정보 없음");
        }

        return new PerformanceDetailResponse(
                performance.getId(),
                performance.getTitle(),
                performance.getPosterUrl(),
                castList,
                performance.getStartDate(),
                performance.getEndDate(),
                performance.getRuntime(),
                performance.getPerformanceStartTime(),
                performance.getVenue(),
                performance.getTicketPrice(),
                performance.getTicketVendor(),
                performance.getTicketUrl()
        );
    }
}
