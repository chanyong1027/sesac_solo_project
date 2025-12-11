package com.stagelog.Stagelog.batch.processor;

import com.stagelog.Stagelog.batch.dto.KopisPerformanceApiDto;
import com.stagelog.Stagelog.domain.KopisPerformance;
import com.stagelog.Stagelog.repository.KopisPerformanceRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceItemProcessor implements ItemProcessor<KopisPerformanceApiDto, KopisPerformance> {

    private final KopisPerformanceRepository kopisPerformanceRepository;

    @Override
    public KopisPerformance process(@Nonnull KopisPerformanceApiDto item) {

        if (isDuplicate(item)) {
            log.debug("중복 아이템 스킵: {} ({})",
                    item.getMt20id(), item.getPrfnm());
            return null;
        }

        log.debug("새로운 아이템 처리: {} ({})",
                item.getMt20id(), item.getPrfnm());
        return item.toEntity();
    }

    private boolean isDuplicate(KopisPerformanceApiDto item) {
        return kopisPerformanceRepository.existsByKopisId(item.getMt20id());
    }
}
