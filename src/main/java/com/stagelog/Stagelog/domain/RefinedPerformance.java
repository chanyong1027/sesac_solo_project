package com.stagelog.Stagelog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefinedPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kopis_id", unique = true, nullable = false)
    private String kopisId;

    @Column(name = "title")
    private String title;

    @Column(name = "poster_url", length = 1000)
    private String posterUrl;

    @Column(name = "place")
    private String venue;

    @Column(name = "state")
    private String status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "genre")
    private String genre;

    @Builder.Default
    @Column(name = "has_detail")
    private boolean hasDetail = false; // 상세 정보 수집 여부

    @Column(name = "prfcast", columnDefinition = "TEXT")
    private String cast;

    @Column(name = "runtime")
    private String runtime;

    @Column(name = "pcseguidance", length = 1000)
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

    @Column(name = "relateurl", columnDefinition = "TEXT")
    private String ticketUrl;

    // ============================================================
    // 아티스트 정보 (artist 테이블에서 가져옴)
    // ============================================================
    @Column(name = "artist_id")
    private Long artistId;

    @Column(name = "artist_name_kr", length = 200)
    private String artistNameKr;  // 한글 아티스트명

    @Column(name = "artist_name_en", length = 200)
    private String artistNameEn;  // 영문 아티스트명

    @Column(name = "artist_genres", columnDefinition = "TEXT")
    private String artistGenres;  // Spotify 장르 정보 (JSON 배열)

    // ==== 메타 정보 ====
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_mapping_id")
    private ArtistMapping artistMapping;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public void setArtistMapping(ArtistMapping artistMapping) {
        this.artistMapping = artistMapping;
    }
}
