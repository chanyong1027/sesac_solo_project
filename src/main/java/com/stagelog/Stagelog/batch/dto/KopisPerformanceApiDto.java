package com.stagelog.Stagelog.batch.dto;

import com.stagelog.Stagelog.domain.KopisPerformance;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class KopisPerformanceApiDto {
    private Long mcode; // 디비에서 구분용 고유 id
    private String mt20id; // 뮤지컬 고유 id
    private String prfnm; // 공연 이름
    private String fcltynm; // 공연장명
    private String prfstate; // 공연 상태
    private String poster; // 포스터
    private LocalDate prfpdfrom; // 공연 시작일
    private LocalDate prfpdto;

    public KopisPerformance toEntity() {
        return KopisPerformance.builder()
                .id(this.mcode)
                .kopisId(this.mt20id)
                .title(this.prfnm)
                .venue(this.fcltynm)
                .status(this.prfstate)
                .posterUrl(this.poster)
                .startDate(this.prfpdfrom)
                .endDate(this.prfpdto)
                .build();
    }
}
