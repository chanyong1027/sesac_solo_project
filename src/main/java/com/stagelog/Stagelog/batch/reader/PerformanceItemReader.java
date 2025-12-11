package com.stagelog.Stagelog.batch.reader;

import com.stagelog.Stagelog.batch.client.KopisPerformanceDataProvider;
import com.stagelog.Stagelog.batch.dto.KopisPerformanceApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class PerformanceItemReader implements ItemReader<KopisPerformanceApiDto> {
    private final KopisPerformanceDataProvider kopisPerformanceDataProvider;

    private static final int MAX_ITEMS_TO_FETCH = 300;

    private int currentPage = 1;
    private List<KopisPerformanceApiDto> performanceBuffer = new ArrayList<>();
    private int nextIndex = 0;
    private boolean isFinished = false;
    private int totalItemRead;

    @Override
    public KopisPerformanceApiDto read() {
        if(totalItemRead == MAX_ITEMS_TO_FETCH) {
            return null;
        }

        if (isFinished) {
            return null;
        }

        if (nextIndex >= performanceBuffer.size()) {

            log.info("kopis 데이터들을 가져오는 중={}", currentPage);
            performanceBuffer = kopisPerformanceDataProvider.fetchPerformances(currentPage);

            if (performanceBuffer == null || performanceBuffer.isEmpty()) {
                log.info("더 가져올 kopis 데이터가 없습니다.");
                isFinished = true;
                return null;
            }

            nextIndex = 0;
            currentPage++;
        }

        KopisPerformanceApiDto nextMusical = performanceBuffer.get(nextIndex);
        nextIndex++;
        totalItemRead++;

        return nextMusical;
    }
}
