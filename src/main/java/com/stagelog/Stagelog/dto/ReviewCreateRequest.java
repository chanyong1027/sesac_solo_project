package com.stagelog.Stagelog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewCreateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private String playlistTitle; // 플레이리스트 제목
    private List<TrackRequest> tracks;

    @Getter
    @NoArgsConstructor
    public static class TrackRequest {
        @NotBlank(message = "Spotify ID는 필수입니다.")
        private String spotifyId;

        @NotBlank(message = "트랙 제목은 필수입니다.")
        private String title;

        @NotBlank(message = "아티스트명은 필수입니다.")
        private String artistName;

        private String albumImageUrl;
        private String spotifyUri;
        private String externalUrl;
        private Long durationMs;
    }
}
