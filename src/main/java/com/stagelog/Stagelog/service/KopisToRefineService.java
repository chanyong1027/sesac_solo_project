package com.stagelog.Stagelog.service;

import com.stagelog.Stagelog.domain.KopisPerformance;
import com.stagelog.Stagelog.domain.RefinedPerformance;
import com.stagelog.Stagelog.repository.KopisPerformanceRepository;
import com.stagelog.Stagelog.repository.RefinedPerformanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KopisToRefineService {

    private final KopisPerformanceRepository kopisRepository;
    private final RefinedPerformanceRepository refinedRepository;

    @Transactional
    public void refineKopisData() {
        // 1. 대상 데이터 조회
        List<KopisPerformance> rawList = kopisRepository.findByHasDetailTrueAndIsRefinedFalse();

        if (rawList.isEmpty()) {
            log.info("정제할 새로운 원본 데이터가 없습니다.");
            return;
        }

        for (KopisPerformance raw : rawList) {
            try {
                // 2. RefinedPerformance로 변환 (아티스트 정보 제외 모든 필드 매핑)
                RefinedPerformance refined = convertToRefined(raw);

                // 3. 정제 테이블에 저장 (이미 있다면 업데이트, 없다면 삽입)
                // kopisId 기준 중복 방지를 위해 기존 데이터를 확인합니다.
                refinedRepository.findByKopisId(raw.getKopisId())
                        .ifPresentOrElse(
                                existing -> updateRefined(existing, refined),
                                () -> refinedRepository.save(refined)
                        );

                // 4. 원본 데이터에 정제 완료 마킹
                raw.markAsRefined();

            } catch (Exception e) {
                log.error("정제 실패 - KopisID: {}, 사유: {}", raw.getKopisId(), e.getMessage());
            }
        }
    }

    private RefinedPerformance convertToRefined(KopisPerformance raw) {
        return RefinedPerformance.builder()
                .kopisId(raw.getKopisId())
                .title(raw.getTitle())
                .posterUrl(raw.getPosterUrl())
                .venue(raw.getVenue())
                .status(raw.getStatus())
                .startDate(raw.getStartDate())
                .endDate(raw.getEndDate())
                .genre(raw.getGenre())
                .hasDetail(raw.getHasDetail())
                .cast(raw.getCast())
                .runtime(raw.getRuntime())
                .ticketPrice(raw.getTicketPrice())
                .area(raw.getArea())
                .performanceStartTime(raw.getPerformanceStartTime())
                .isVisit(raw.isVisit())
                .isFestival(raw.isFestival())
                .ticketVendor(raw.getTicketVendor())
                .ticketUrl(raw.getTicketUrl())
                // artistId 등 아티스트 정보는 여기서 넣지 않음 (원본에 없으므로)
                .build();
    }

    private void updateRefined(RefinedPerformance existing, RefinedPerformance newValue) {
        existing.setTitle(newValue.getTitle());
        existing.setStatus(newValue.getStatus());
        existing.setEndDate(newValue.getEndDate());
        // ... 필요한 필드만 업데이트 수행
    }
}
