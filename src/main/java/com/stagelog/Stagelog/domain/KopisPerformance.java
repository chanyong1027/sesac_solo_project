package com.stagelog.Stagelog.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "prfcast")
    private String cast;

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
