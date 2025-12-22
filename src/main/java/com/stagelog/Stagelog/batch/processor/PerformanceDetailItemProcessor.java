package com.stagelog.Stagelog.batch.processor;

import com.stagelog.Stagelog.batch.client.KopisPerformanceDataProvider;
import com.stagelog.Stagelog.domain.KopisPerformance;
import com.stagelog.Stagelog.dto.RealKopisPerformanceDetailResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceDetailItemProcessor implements ItemProcessor<KopisPerformance, KopisPerformance>{

    private final KopisPerformanceDataProvider kopisPerformanceDataProvider;
    private static final long RATE_LIMIT_DELAY_MS = 200;

    @Override
    public KopisPerformance process(KopisPerformance item) throws Exception {
        try {
            Thread.sleep(RATE_LIMIT_DELAY_MS);

            RealKopisPerformanceDetailResponseDto detailResponseDto = kopisPerformanceDataProvider.fetchPerformanceDetail(item.getKopisId());

            if(detailResponseDto == null){
                log.warn("상세정보를 찾을 수 없음: {}", item.getKopisId());
                item.handleNoDetail();
                return item;
            }

            item.updateDetailInfo(detailResponseDto);
            return item;
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Rate limiting 중 인터럽트 발생");
        }
    }
}
