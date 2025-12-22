package com.stagelog.Stagelog.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record SearchTrackResponse(
        TracksContainer tracks
) {
    public record TracksContainer(
            List<TrackDto> items
    ) {}

    public record TrackDto(
            String id,
            String name,
            List<ArtistDto> artists,
            AlbumDto album,
            @JsonProperty("duration_ms") long durationMs,
            @JsonProperty("external_urls") Map<String, String> externalUrls,
            @JsonProperty("preview_url") String previewUrl
    ) {
        // 첫 번째 가수의 이름을 가져오는 편의 메서드
        public String getFirstArtistName() {
            return artists.isEmpty() ? "Unknown" : artists.get(0).name();
        }

        // 중간 사이즈(300x300) 이미지를 가져오는 편의 메서드
        public String getMediumImageUrl() {
            if (album.images().size() >= 2) return album.images().get(1).url();
            return album.images().isEmpty() ? null : album.images().get(0).url();
        }
    }

    public record ArtistDto(
            String id,
            String name
    ) {}

    public record AlbumDto(
            String id,
            String name,
            List<ImageDto> images
    ) {}

    public record ImageDto(
            String url,
            int height,
            int width
    ) {}
}
