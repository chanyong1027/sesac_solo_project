package com.stagelog.Stagelog.service;

import com.stagelog.Stagelog.domain.ArtistMapping;
import com.stagelog.Stagelog.domain.KopisPerformance;
import com.stagelog.Stagelog.domain.RefinedPerformance;
import com.stagelog.Stagelog.repository.ArtistMappingRepository;
import com.stagelog.Stagelog.repository.KopisPerformanceRepository;
import com.stagelog.Stagelog.repository.RefinedPerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*
*정제 공연 데이터에서 아티스트를 분리하기 위한 서비스
 */
@Service
@RequiredArgsConstructor
public class MigrationService {
    private final RefinedPerformanceRepository refinedPerformanceRepository;
    private final ArtistMappingRepository artistMappingRepository;
    private final KopisPerformanceRepository kopisPerformanceRepository;

    @Transactional
    public void migrateArtists() {
        // 1. 모든 공연 데이터를 가져옴
        List<RefinedPerformance> performances = refinedPerformanceRepository.findAll();

        // 2. 중복 생성을 방지하기 위한 맵 (Key: 영어이름 또는 한글이름)
        Map<String, ArtistMapping> artistMap = new HashMap<>();

        for (RefinedPerformance pf : performances) {
            if (pf.getArtistMapping() != null)
                continue;
            // 아티스트 정보가 없는 데이터는 건너뜀
            if (pf.getArtistNameEn() == null && pf.getArtistNameKr() == null)
                continue;

            // 유니크한 키 생성 (여기서는 영어이름을 우선순위로 사용)
            String artistKey = (pf.getArtistNameEn() != null) ? pf.getArtistNameEn() : pf.getArtistNameKr();

            // 3. 맵에 없으면 새로 생성 후 저장
            if (!artistMap.containsKey(artistKey)) {
                ArtistMapping newArtist = ArtistMapping.create(
                        pf.getArtistNameKr(),
                        pf.getArtistNameEn(),
                        pf.getArtistGenres());
                artistMappingRepository.save(newArtist);
                artistMap.put(artistKey, newArtist);
            }

            // 4. 공연 엔티티에 새로 만든 아티스트를 연결
            pf.setArtistMapping(artistMap.get(artistKey));
        }
    }

    @Transactional
    public void migrateByArtistMatching() {
        // 1. 모든 아티스트를 미리 메모리에 올림 (200개는 매우 적어서 효율적임)
        List<ArtistMapping> artists = artistMappingRepository.findAll();
        List<KopisPerformance> raws = kopisPerformanceRepository.findAll(); // 원본 공연
        List<RefinedPerformance> results = new ArrayList<>();

        // 2. 이미 정제된 공연의 kopisId를 Set으로 조회 (중복 저장 방지)
        Set<String> existingKopisIds = refinedPerformanceRepository.findAll().stream()
                .map(RefinedPerformance::getKopisId)
                .collect(Collectors.toSet());

        for (KopisPerformance raw : raws) {
            // 이미 정제된 공연은 건너뛰기
            if (existingKopisIds.contains(raw.getKopisId())) {
                continue;
            }

            String title = raw.getTitle();

            for (ArtistMapping artist : artists) {
                if (isMatch(title, artist)) {
                    results.add(convertToRefined(raw, artist));
                    break; // 매칭되면 다음 공연으로
                }
            }
        }
        // 3. 한꺼번에 저장 (Batch Insert 효과)
        refinedPerformanceRepository.saveAll(results);
    }

    private boolean isMatch(String title, ArtistMapping artist) {
        // 한글명, 영문명, 별칭 중 하나라도 포함되면 true
        if (artist.getNameKr() != null && title.contains(artist.getNameKr()))
            return true;
        if (artist.getNameEn() != null && title.toUpperCase().contains(artist.getNameEn().toUpperCase()))
            return true;

        if (artist.getAlias() != null && !artist.getAlias().isEmpty()) {
            for (String a : artist.getAlias().split(",")) {
                if (title.contains(a.trim()))
                    return true;
            }
        }
        return false;
    }

    private RefinedPerformance convertToRefined(KopisPerformance raw, ArtistMapping artist) {
        // ArtistMapping의 장르 리스트를 JSON 형태나 콤마 구분자 문자열로 변환
        String genresJson = (artist.getGenres() != null) ? String.join(", ", artist.getGenres()) : "";

        return RefinedPerformance.builder()
                // (1) 공연 기본 정보 매핑 (원본 데이터에서 가져옴)
                .kopisId(raw.getKopisId())
                .title(raw.getTitle())
                .posterUrl(raw.getPosterUrl())
                .venue(raw.getVenue())
                .status(raw.getStatus())
                .startDate(raw.getStartDate()) // 날짜 형식(yyyy-MM-dd) 주의
                .endDate(raw.getEndDate())
                .genre(raw.getGenre())
                .cast(raw.getCast())
                .runtime(raw.getRuntime())
                .ticketPrice(raw.getTicketPrice())
                .area(raw.getArea())
                .performanceStartTime(raw.getPerformanceStartTime())
                .ticketVendor(raw.getTicketVendor())
                .ticketUrl(raw.getTicketUrl())
                .isVisit(raw.isVisit())
                .isFestival(raw.isFestival())
                .hasDetail(raw.getHasDetail())

                // (2) 아티스트 정보 매핑 (ArtistMapping에서 가져옴)
                .artistId(artist.getId())
                .artistNameKr(artist.getNameKr())
                .artistNameEn(artist.getNameEn())
                .artistGenres(genresJson)

                // (3) 연관 관계 설정
                .artistMapping(artist)
                .build();
    }
}
