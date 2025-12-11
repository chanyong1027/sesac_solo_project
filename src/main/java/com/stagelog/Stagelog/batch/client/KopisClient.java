package com.stagelog.Stagelog.batch.client;

import com.stagelog.Stagelog.api.KopisApi;
import com.stagelog.Stagelog.batch.dto.KopisPerformanceApiDto;
import com.stagelog.Stagelog.dto.KopisPerformanceDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KopisClient implements KopisPerformanceDataProvider {

    private final KopisApi kopisApi;

    @Override
    public List<KopisPerformanceApiDto> fetchPerformances(int page) {
        return kopisApi.fetchPerformances(page);
    }

    @Override
    public KopisPerformanceDetailResponseDto fetchPerformanceDetail(String kopisId) {
        return kopisApi.fetchMusicalDetail(kopisId);
    }
}
