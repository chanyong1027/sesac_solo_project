package com.stagelog.Stagelog.dto;

import com.stagelog.Stagelog.domain.InterestedPerformance;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class IPListResponse {
    private Long id;              // 관심 등록 고유 ID (삭제 시 필요)
    private Long performanceId;    // 공연 상세 페이지 이동 시 필요
    private String title;          // 공연 제목
    private String posterUrl;      // 포스터 이미지
    private String venue;          // 공연 장소 (추가 추천)
    private String status;         // 공연 상태 (공연중, 공연예정 등 - 추가 추천)
    private LocalDate startDate;   // 시작일
    private LocalDate endDate;     // 종료일
    private String cast;           // 출연진

    // 엔티티를 DTO로 변환하는 생성자
    public IPListResponse(InterestedPerformance ip) {
        this.id = ip.getId();
        this.performanceId = ip.getPerformance().getId();
        this.title = ip.getPerformance().getTitle();
        this.posterUrl = ip.getPerformance().getPosterUrl();
        this.venue = ip.getPerformance().getVenue();
        this.status = ip.getPerformance().getStatus();
        this.startDate = ip.getPerformance().getStartDate();
        this.endDate = ip.getPerformance().getEndDate();
        this.cast = ip.getPerformance().getCast();
    }
}
