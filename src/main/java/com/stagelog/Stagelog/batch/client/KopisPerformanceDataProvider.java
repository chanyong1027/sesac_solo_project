package com.stagelog.Stagelog.batch.client;


import com.stagelog.Stagelog.batch.dto.KopisPerformanceApiDto;
import com.stagelog.Stagelog.dto.PerformanceDetailResponseDto;

import java.util.List;

public interface KopisPerformanceDataProvider {
    List<KopisPerformanceApiDto> fetchPerformances(String startDate, String endDate, int currentPage, String category);
    PerformanceDetailResponseDto fetchPerformanceDetail(String mt20id);
}
