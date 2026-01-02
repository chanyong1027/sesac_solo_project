package com.stagelog.Stagelog.domain;

import com.stagelog.Stagelog.dto.ReviewCreateRequest;
import com.stagelog.Stagelog.dto.ReviewUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spotifyId;
    private String title;
    private String artistName;
    private String albumImageUrl;
    private String spotifyUri;
    private String externalUrl;
    private Long durationMs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    // ============================================================
    // 정적 팩토리 메서드
    // ============================================================

    /**
     * ReviewCreateRequest.TrackRequest로부터 Track 생성
     */
    public static Track from(ReviewCreateRequest.TrackRequest trackRequest) {
        return Track.builder()
                .spotifyId(trackRequest.getSpotifyId())
                .title(trackRequest.getTitle())
                .artistName(trackRequest.getArtistName())
                .albumImageUrl(trackRequest.getAlbumImageUrl())
                .spotifyUri(trackRequest.getSpotifyUri())
                .externalUrl(trackRequest.getExternalUrl())
                .durationMs(trackRequest.getDurationMs())
                .build();
    }

    /**
     * ReviewUpdateRequest.TrackRequest로부터 Track 생성
     */
    public static Track from(ReviewUpdateRequest.TrackRequest trackRequest) {
        return Track.builder()
                .spotifyId(trackRequest.getSpotifyId())
                .title(trackRequest.getTitle())
                .artistName(trackRequest.getArtistName())
                .albumImageUrl(trackRequest.getAlbumImageUrl())
                .spotifyUri(trackRequest.getSpotifyUri())
                .externalUrl(trackRequest.getExternalUrl())
                .durationMs(trackRequest.getDurationMs())
                .build();
    }

    // ============================================================
    // 연관관계 메서드
    // ============================================================

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}
