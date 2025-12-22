package com.stagelog.Stagelog.batch.client;

import com.stagelog.Stagelog.batch.api.KopisApi;
import com.stagelog.Stagelog.batch.dto.KopisPerformanceApiDto;
import com.stagelog.Stagelog.dto.RealKopisPerformanceDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KopisClient implements KopisPerformanceDataProvider {

    private final KopisApi kopisApi;

    @Override
    public List<KopisPerformanceApiDto> fetchPerformances(String startDate, String endDate, int currentPage, String category) {
        return kopisApi.fetchPerformances(startDate, endDate, currentPage, category);
    }

    @Override
    public RealKopisPerformanceDetailResponseDto fetchPerformanceDetail(String kopisId) {
        return kopisApi.fetchMusicalDetail(kopisId);
    }
}
