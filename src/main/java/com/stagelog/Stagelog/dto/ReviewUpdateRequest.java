package com.stagelog.Stagelog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewUpdateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private String playlistTitle; // 플레이리스트 제목
    private List<TrackRequest> tracks;

    @Getter
    @NoArgsConstructor
    public static class TrackRequest {
        @NotBlank
        private String spotifyId;

        private String title;
        private String artistName;
        private String albumImageUrl;
        private String previewUrl;
        private String spotifyUri;
        private String externalUrl;
        private Long durationMs;
    }
}
