package com.stagelog.Stagelog.spotify.dto;

import lombok.*;

import java.time.LocalDate;

public class RefinedPerformanceAdminDto {
    /**
     * [요청] 공연 정보 수정
     */
    @Getter @Setter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String artistNameKr;
    }

    /**
     * [응답] 공연 정보
     */
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String kopisId;
        private String title;
        private String venue;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private String genre;

        // 아티스트 정보
        private String artistNameKr;
        private String artistNameEn;
        private String artistGenres;

        // 추가 정보
        private String posterUrl;
    }

    /**
     * [응답] 통계
     */
    @Getter
    @AllArgsConstructor
    public static class Stats {
        private final long total;
        private final long withArtist;
        private final long withoutArtist;
    }
}
