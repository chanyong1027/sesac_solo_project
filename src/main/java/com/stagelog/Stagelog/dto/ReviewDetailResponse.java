package com.stagelog.Stagelog.dto;

import com.stagelog.Stagelog.domain.Review;
import com.stagelog.Stagelog.domain.Track;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
// @NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewDetailResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String playlistTitle; // 플레이리스트 제목 추가
    private List<TrackDto> tracks;

    @Getter
    @Builder
    public static class TrackDto {
        private Long id;
        private String title;
        private String artistName;
        private String albumImageUrl;
        private String spotifyId;
        private final Long durationMs;
        private final String spotifyUri;
        private final String externalUrl;

        public static TrackDto from(Track track) {
            return TrackDto.builder()
                    .id(track.getId())
                    .title(track.getTitle())
                    .artistName(track.getArtistName())
                    .albumImageUrl(track.getAlbumImageUrl())
                    .spotifyId(track.getSpotifyId())
                    .durationMs(track.getDurationMs())
                    .spotifyUri(track.getSpotifyUri())
                    .externalUrl(track.getExternalUrl())
                    .build();
        }
    }

    public static ReviewDetailResponse from(Review review) {
        // 플레이리스트가 없으면 빈 리스트 반환
        List<TrackDto> trackList = Collections.emptyList();
        String playlistName = null;

        if (review.getPlaylist() != null) {
            playlistName = review.getPlaylist().getTitle();
            trackList = review.getPlaylist().getTracks().stream()
                    .map(TrackDto::from)
                    .toList();
        }

        return ReviewDetailResponse.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .playlistTitle(playlistName)
                .tracks(trackList)
                .build();
    }

}
