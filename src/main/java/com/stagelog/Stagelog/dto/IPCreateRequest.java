package com.stagelog.Stagelog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IPCreateRequest {
    @NotNull(message = "공연 ID는 필수입니다.")
    private Long performanceId;
}
