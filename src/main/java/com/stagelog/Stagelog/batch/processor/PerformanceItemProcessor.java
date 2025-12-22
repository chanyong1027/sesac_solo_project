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
        KopisPerformance existingPerformance = findExisting(item);

        if (existingPerformance != null) {
            log.debug("중복 아이템 공연 상태 업데이트: {} ({})",
                    item.getMt20id(), item.getPrfnm());

            // 기존 엔티티의 상태(status)만 최신 정보로 업데이트
            existingPerformance.updateStatus(item.getPrfstate());

            // null을 리턴하면 배치가 스킵하지만, 기존 객체를 리턴하면 Writer로 넘어가서 Update됩니다.
            return existingPerformance;
        }

        log.debug("새로운 아이템 처리: {} ({})",
                item.getMt20id(), item.getPrfnm());
        return item.toEntity();
    }

    private KopisPerformance findExisting(KopisPerformanceApiDto item) {
        return kopisPerformanceRepository.findByKopisId(item.getMt20id())
                .orElse(null);
    }
}
