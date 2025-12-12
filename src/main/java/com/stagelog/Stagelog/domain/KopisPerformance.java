package com.stagelog.Stagelog.domain;

import com.stagelog.Stagelog.dto.PerformanceDetailResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Entity
@Table(name = "raw_performance")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KopisPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mt20id", unique = true, nullable = false)
    private String kopisId;

    @Column(name = "prfnm")
    private String title;

    @Column(name = "poster")
    private String posterUrl;

    @Column(name = "fcltynm")
    private String venue;

    @Column(name = "prfstate")
    private String status;

    @Column(name = "prfpdfrom")
    private LocalDate startDate;

    @Column(name = "prfpdto")
    private LocalDate endDate;

    @Column(name = "genrenm")
    private String genre;

    @Column(name = "has_detail")
    private Boolean hasDetail = false; // 상세 정보 수집 여부

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --------상세 정보로 받아 올 필드들--------

    @Column(name = "prfcast")
    private String cast;

    @Column(name = "prfruntime")
    private String runtime;

    @Column(name = "pcseguidance")
    private String ticketPrice;

    @Column(name = "area")
    private String area;

    @Column(name = "dtguidance")
    private String performanceStartTime;

    @Column(name = "visit")
    private boolean isVisit;

    @Column(name = "festival")
    private boolean isFestival;

    @Column(name = "relatenm")
    private String ticketVendor;

    @Column(name = "relateurl")
    private String ticketUrl;

    public void updateDetailInfo(PerformanceDetailResponseDto detail) {
        if (detail == null) {
            throw new IllegalArgumentException("상세 정보가 null일 수 없습니다");
        }

        this.title = detail.getPrfnm();
        this.cast = detail.getPrfcast();
        this.runtime = detail.getPrfruntime();
        this.ticketPrice = detail.getPcseguidance();
        this.performanceStartTime = detail.getDtguidance();
        this.area = detail.getArea();
        this.startDate = detail.getStartDate();
        this.endDate = detail.getEndDate();
        this.hasDetail = true;
        this.isFestival = detail.isFestival();
        this.isVisit = detail.isVisitPerformance();
        this.ticketVendor = detail.getRelates().stream()
                .map(PerformanceDetailResponseDto.RelateDto::getRelatenm)
                .collect(Collectors.joining(", "));
        this.ticketUrl = detail.getTicketLinks().stream()
                .map(PerformanceDetailResponseDto.RelateDto::getRelateurl)
                .filter(url -> url != null && !url.isEmpty())
                .collect(Collectors.joining(", "));
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
