package com.stagelog.Stagelog.migration.domain;

import com.stagelog.Stagelog.domain.ArtistMapping;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "refined_performance_v2")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RefinedPerformanceV2 {

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
    private boolean hasDetail = false;

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
    // 아티스트 정보 (기존과 동일)
    // ============================================================
    @Column(name = "artist_id")
    private Long artistId;

    @Column(name = "artist_name_kr", length = 200)
    private String artistNameKr;

    @Column(name = "artist_name_en", length = 200)
    private String artistNameEn;

    @Column(name = "artist_genres", columnDefinition = "TEXT")
    private String artistGenres;

    // ============================================================
    // 화이트리스트 매칭 추적 정보 (신규)
    // ============================================================

    /**
     * 어떤 TargetArtistV2에 의해 매칭되었는지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_artist_id")
    private TargetArtistV2 matchedArtist;

    /**
     * 어떤 키워드로 매칭되었는지 (예: "잔나비", "JANNABI")
     */
    @Column(name = "matched_keyword", length = 200)
    private String matchedKeyword;

    /**
     * 필터링 상태 (WHITELIST_MATCHED, MANUAL_ADDED 등)
     */
    @Column(name = "filter_status", length = 20)
    private String filterStatus = "WHITELIST_MATCHED";

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

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ============================================================
    // 비즈니스 메서드
    // ============================================================

    /**
     * 매칭 정보 설정
     */
    public void setMatchingInfo(TargetArtistV2 matchedArtist, String matchedKeyword) {
        this.matchedArtist = matchedArtist;
        this.matchedKeyword = matchedKeyword;
        this.filterStatus = "WHITELIST_MATCHED";
    }

    /**
     * 한글 아티스트명 수정
     */
    public void updateArtistNameKr(String artistNameKr) {
        this.artistNameKr = artistNameKr;
    }

    /**
     * Spotify 아티스트 정보 업데이트
     */
    public void updateSpotifyArtistInfo(String artistNameEn, String artistGenres) {
        this.artistNameEn = artistNameEn;
        this.artistGenres = artistGenres;
    }

    /**
     * ArtistMapping 연관관계 설정
     */
    public void linkArtistMapping(ArtistMapping artistMapping) {
        this.artistMapping = artistMapping;
    }

    /**
     * KOPIS 원본 데이터와 동기화
     */
    public void syncWithKopisData(
            String title, String posterUrl, String venue, String status,
            LocalDate startDate, LocalDate endDate, String genre, boolean hasDetail,
            String cast, String runtime, String ticketPrice, String area,
            String performanceStartTime, boolean isVisit, boolean isFestival,
            String ticketVendor, String ticketUrl
    ) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.venue = venue;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.genre = genre;
        this.hasDetail = hasDetail;
        this.cast = cast;
        this.runtime = runtime;
        this.ticketPrice = ticketPrice;
        this.area = area;
        this.performanceStartTime = performanceStartTime;
        this.isVisit = isVisit;
        this.isFestival = isFestival;
        this.ticketVendor = ticketVendor;
        this.ticketUrl = ticketUrl;
    }
}
