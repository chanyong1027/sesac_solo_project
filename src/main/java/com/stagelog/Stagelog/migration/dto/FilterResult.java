package com.stagelog.Stagelog.migration.dto;

import com.stagelog.Stagelog.domain.KopisPerformance;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FilterResult {
    private final List<KopisPerformance> selectedPerformances;
    private final int totalCount;
    private final int selectedCount;
    private final int excludedCount;
    private final double selectRate;

    public static FilterResult of(List<KopisPerformance> selected, int total) {
        int selectedCount = selected.size();
        int excludedCount = total - selectedCount;

        return FilterResult.builder()
            .selectedPerformances(selected)
            .totalCount(total)
            .selectedCount(selectedCount)
            .excludedCount(excludedCount)
            .selectRate(total > 0 ? (selectedCount * 100.0) / total : 0.0)
            .build();
    }

    public static FilterResult empty() {
        return FilterResult.builder()
            .selectedPerformances(List.of())
            .totalCount(0)
            .selectedCount(0)
            .excludedCount(0)
            .selectRate(0.0)
            .build();
    }
}
