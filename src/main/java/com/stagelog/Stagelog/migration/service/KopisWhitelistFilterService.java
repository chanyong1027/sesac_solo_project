package com.stagelog.Stagelog.migration.service;

import com.stagelog.Stagelog.domain.KopisPerformance;
import com.stagelog.Stagelog.migration.domain.TargetArtistV2;
import com.stagelog.Stagelog.migration.dto.FilterResult;
import com.stagelog.Stagelog.migration.repository.TargetArtistV2Repository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KopisWhitelistFilterService {

    private final TargetArtistV2Repository targetArtistRepository;

    /**
     * 화이트리스트 기반 필터링: 공연 제목에 타겟 아티스트 이름이 포함되면 선택
     * 한글이름, 영어이름, 별칭 모두 검색하여 매칭
     */
    @Transactional
    public FilterResult filterByWhitelist(List<KopisPerformance> performances) {

        log.info("=== 화이트리스트 필터링 시작 ===");
        log.info("입력 공연 수: {}개", performances.size());

        // 1. 활성화된 타겟 아티스트 목록 조회
        List<TargetArtistV2> activeArtists = targetArtistRepository.findByIsActiveTrue();

        if (activeArtists.isEmpty()) {
            log.warn("활성화된 타겟 아티스트가 없습니다.");
            return FilterResult.empty();
        }

        log.info("활성화된 타겟 아티스트: {}명", activeArtists.size());

        // 2. 매칭 카운트 추적용 Set (중복 방지)
        Set<Long> matchedArtistIds = new HashSet<>();

        // 3. 필터링: 제목에 아티스트 이름이 포함된 공연만 선택
        List<KopisPerformance> filtered = performances.stream()
            .filter(performance -> {
                String title = performance.getTitle();

                // 제목에 포함된 타겟 아티스트 찾기
                Optional<TargetArtistV2> matched = activeArtists.stream()
                    .filter(artist -> artist.matchesTitle(title))
                    .findFirst();

                if (matched.isPresent()) {
                    matchedArtistIds.add(matched.get().getId());
                    log.debug("매칭됨: [{}] - 아티스트: {}", title, matched.get().getArtistNameKr());
                    return true;
                }
                return false;
            })
            .collect(Collectors.toList());

        // 4. 매칭된 아티스트의 카운트 업데이트
        matchedArtistIds.forEach(artistId -> {
            targetArtistRepository.findById(artistId).ifPresent(TargetArtistV2::incrementMatchCount);
        });

        // 5. 결과 로깅
        FilterResult result = FilterResult.of(filtered, performances.size());

        log.info("=== 화이트리스트 필터링 완료 ===");
        log.info("선택: {}개 ({}%)", result.getSelectedCount(), String.format("%.2f", result.getSelectRate()));
        log.info("제외: {}개 ({}%)", result.getExcludedCount(), String.format("%.2f", 100 - result.getSelectRate()));
        log.info("매칭된 아티스트: {}명", matchedArtistIds.size());

        return result;
    }

    /**
     * 어떤 아티스트에 의해 매칭되었는지 확인 (디버깅/관리용)
     */
    @Transactional(readOnly = true)
    public Map<String, List<String>> getMatchedPerformancesByArtist(List<KopisPerformance> performances) {

        List<TargetArtistV2> activeArtists = targetArtistRepository.findByIsActiveTrue();

        return activeArtists.stream()
            .collect(Collectors.toMap(
                TargetArtistV2::getArtistNameKr,
                artist -> performances.stream()
                    .filter(p -> artist.matchesTitle(p.getTitle()))
                    .map(KopisPerformance::getTitle)
                    .collect(Collectors.toList())
            ))
            .entrySet().stream()
            .filter(entry -> !entry.getValue().isEmpty()) // 매칭된 것만
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 특정 공연의 매칭 정보 조회 (어떤 아티스트, 어떤 키워드로 매칭되었는지)
     */
    @Transactional(readOnly = true)
    public MatchInfo getMatchInfo(KopisPerformance performance) {
        List<TargetArtistV2> activeArtists = targetArtistRepository.findByIsActiveTrue();

        for (TargetArtistV2 artist : activeArtists) {
            List<String> keywords = artist.getAllSearchKeywords();

            for (String keyword : keywords) {
                if (performance.getTitle().contains(keyword)) {
                    return new MatchInfo(artist, keyword);
                }
            }
        }

        return null;
    }

    @Getter
    @AllArgsConstructor
    public static class MatchInfo {
        private TargetArtistV2 matchedArtist;
        private String matchedKeyword;
    }
}
