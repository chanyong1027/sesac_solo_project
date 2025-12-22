package com.stagelog.Stagelog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    // private Long performanceId;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    private Review(User user, String title, String content, Playlist playlist) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.playlist = playlist;
    }

    public static Review create(User user, String title, String content, Playlist playlist) {
        return new Review(user, title, content, playlist);
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 내용 수정 편의 메서드
    public void update(String title, String content, String playlistTitle, List<Track> newTracks) {
        this.title = title;
        this.content = content;

        // 새 트랙이 없거나 비어있으면 playlist를 null로
        if (newTracks == null || newTracks.isEmpty()) {
            this.playlist = null;
            return;
        }

        // playlist가 없으면 새로 생성
        if (this.playlist == null) {
            String plTitle = (playlistTitle != null && !playlistTitle.isEmpty())
                    ? playlistTitle
                    : this.title + "의 플레이리스트";
            this.playlist = new Playlist(plTitle);
        } else {
            // 기존 playlist 제목 업데이트
            this.playlist.updateTitle(playlistTitle);
        }

        this.playlist.updateTracks(newTracks);
    }
}
