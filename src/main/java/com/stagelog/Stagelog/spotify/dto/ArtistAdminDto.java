package com.stagelog.Stagelog.spotify.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

public class ArtistAdminDto {

    /**
     * [요청] 화면에서 수정할 때 보낼 데이터
     * nameKr만 입력하면 됨
     */
    @Getter @Setter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String nameKr;
    }

    /**
     * [응답] 화면에 보여줄 데이터
     */
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;          // 원본 이름 (KOPIS)
        private String sampleSource;  // 출처 확인용
        private String sampleKopisId;

        private String nameKr;        // 관리자가 입력한 한글 이름
        private String searchStatus;  // 현재 상태

        private String spotifyGenres; // 참고용 장르
        private String nameEn;        // 참고용 영어 이름
        private Integer spotifyPopularity;
    }

    /**
     * [응답] 통계 데이터
     */
    @Getter @AllArgsConstructor
    public static class Stats {
        private final long found;
        private final long needRetry;
        private final long notFound;
        private final long completed;
    }
}
