package com.stagelog.Stagelog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "artists")
@Getter @Setter
@NoArgsConstructor
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // 나중에 채울 것들 (NULL 허용)
    @Column(name = "spotify_id")
    private String spotifyId;

    @Column(name = "spotify_genres", columnDefinition = "TEXT")
    private String spotifyGenres;

    @Column(name = "spotify_popularity")
    private Integer spotifyPopularity;

    @Column(name = "spotify_image_url", length = 500)
    private String spotifyImageUrl;

    @Column(name = "name_kr")
    private String nameKr;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //데이터 검증용
    @Column(name = "source_title", length = 500)
    private String sourceTitle;

    @Column(name = "source_kopis_id", length = 50)
    private String sourceKopisId;

    @Column(name = "search_status", length = 20)
    private String searchStatus;

    @Column(name = "search_attempts")
    private Integer searchAttempts = 0;

    @Column(name = "last_searched_at")
    private LocalDateTime lastSearchedAt;

    @Column(name = "search_error", length = 500)
    private String searchError;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

   /* public void setName(String name) {
        this.name = name;
    }

    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }

    public void setSourceKopisId(String sourceKopisId) {
        this.sourceKopisId = sourceKopisId;
    }*/
}
