package com.stagelog.Stagelog.batch.client;


import com.stagelog.Stagelog.batch.dto.KopisPerformanceApiDto;
import com.stagelog.Stagelog.dto.KopisPerformanceDetailResponseDto;

import java.util.List;

public interface KopisPerformanceDataProvider {
    List<KopisPerformanceApiDto> fetchPerformances(int page);
    KopisPerformanceDetailResponseDto fetchPerformanceDetail(String mt20id);
}
