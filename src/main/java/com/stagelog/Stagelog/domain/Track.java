package com.stagelog.Stagelog.domain;

import com.stagelog.Stagelog.dto.ReviewCreateRequest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    public Track(String spotifyId, String title,
                 String artistName, String albumImageUrl,
                 String spotifyUri, String externalUrl,  Long durationMs) {
        this.spotifyId = spotifyId;
        this.title = title;
        this.artistName = artistName;
        this.albumImageUrl = albumImageUrl;
        this.spotifyUri = spotifyUri;
        this.externalUrl = externalUrl;
        this.durationMs = durationMs;
    }

    public static Track create(ReviewCreateRequest.TrackRequest trackRequest) {
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

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }


}
