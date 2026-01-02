package com.stagelog.Stagelog.migration.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(
    name = "target_artist_v2",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"artist_name_kr", "artist_name_en"})
    },
    indexes = {
        @Index(name = "idx_target_artist_active", columnList = "is_active"),
        @Index(name = "idx_target_artist_kr", columnList = "artist_name_kr"),
        @Index(name = "idx_target_artist_en", columnList = "artist_name_en")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TargetArtistV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artist_name_kr", nullable = false, length = 200)
    private String artistNameKr;  // 한글 이름

    @Column(name = "artist_name_en", nullable = false, length = 200)
    private String artistNameEn;  // 영어 이름

    @Column(name = "alias", columnDefinition = "TEXT")
    private String alias;  // 별칭들 (파이프 구분)

    @Column(name = "is_active")
    private Boolean isActive = true;  // 활성화 여부

    @Column(name = "match_count")
    private Integer matchCount = 0;  // 매칭된 공연 수 (통계용)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
     * 타겟 아티스트 생성
     */
    public static TargetArtistV2 create(String artistNameKr, String artistNameEn, String alias) {
        TargetArtistV2 artist = new TargetArtistV2();
        artist.artistNameKr = artistNameKr.trim();
        artist.artistNameEn = artistNameEn.trim();
        artist.alias = (alias != null) ? alias.trim() : "";
        artist.isActive = true;
        artist.matchCount = 0;
        return artist;
    }

    /**
     * 모든 검색 키워드 반환 (한글이름, 영어이름, 별칭들)
     */
    public List<String> getAllSearchKeywords() {
        List<String> keywords = new ArrayList<>();

        // 한글 이름
        if (artistNameKr != null && !artistNameKr.isEmpty()) {
            keywords.add(artistNameKr);
        }

        // 영어 이름
        if (artistNameEn != null && !artistNameEn.isEmpty()) {
            keywords.add(artistNameEn);
        }

        // 별칭들
        if (alias != null && !alias.isEmpty()) {
            String[] aliases = alias.split("\\|");
            Arrays.stream(aliases)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(keywords::add);
        }

        return keywords;
    }

    /**
     * 공연 제목에 아티스트 이름이 포함되는지 확인
     */
    public boolean matchesTitle(String performanceTitle) {
        if (performanceTitle == null || performanceTitle.isEmpty()) {
            return false;
        }

        return getAllSearchKeywords().stream()
            .anyMatch(performanceTitle::contains);
    }

    /**
     * 매칭 카운트 증가 (통계)
     */
    public void incrementMatchCount() {
        this.matchCount++;
    }

    /**
     * 비활성화 (필터링에서 제외)
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 활성화 (필터링에 포함)
     */
    public void activate() {
        this.isActive = true;
    }
}
