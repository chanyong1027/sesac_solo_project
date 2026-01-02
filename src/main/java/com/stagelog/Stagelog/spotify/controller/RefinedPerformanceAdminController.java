package com.stagelog.Stagelog.spotify.controller;

import com.stagelog.Stagelog.domain.RefinedPerformance;
import com.stagelog.Stagelog.global.exception.EntityNotFoundException;
import com.stagelog.Stagelog.global.exception.ErrorCode;
import com.stagelog.Stagelog.repository.RefinedPerformanceRepository;
import com.stagelog.Stagelog.spotify.dto.RefinedPerformanceAdminDto;
import com.stagelog.Stagelog.spotify.service.SpotifyArtistManualMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/performances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RefinedPerformanceAdminController {
    private final RefinedPerformanceRepository performanceRepository;
    private final SpotifyArtistManualMatchService spotifyArtistManualMatchService;

    /**
     * 전체 공연 목록 조회 (페이지네이션)
     */
    @GetMapping
    public ResponseEntity<List<RefinedPerformanceAdminDto.Response>> getPerformances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // 간단한 페이지네이션 (실제로는 Pageable 사용 권장)
       /* List<RefinedPerformance> performances = performanceRepository.findAll();

        int start = page * size;
        int end = Math.min(start + size, performances.size());

        if (start >= performances.size()) {
            return ResponseEntity.ok(List.of());
        }

        List<RefinedPerformanceAdminDto.Response> response = performances.subList(start, end).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());*/

        List<RefinedPerformanceAdminDto.Response> response = performanceRepository.findAll(pageable)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 아티스트명 없는 공연 조회
     */
    @GetMapping("/no-artist")
    public ResponseEntity<List<RefinedPerformanceAdminDto.Response>> getPerformancesWithoutArtist() {
        Sort sort = Sort.by("createdAt").descending();
        List<RefinedPerformance> performances = performanceRepository.findByArtistNameKrIsNull(sort);

        List<RefinedPerformanceAdminDto.Response> response = performances.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 검색 (제목 또는 아티스트명)
     */
    @GetMapping("/search")
    public ResponseEntity<List<RefinedPerformanceAdminDto.Response>> searchPerformances(
            @RequestParam String keyword
    ) {
        List<RefinedPerformance> performances = performanceRepository.searchByTitleOrArtist(keyword);

        List<RefinedPerformanceAdminDto.Response> response = performances.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 공연 정보 업데이트 (artist_name_kr)
     */
    @PutMapping("/{id}")
    public ResponseEntity<RefinedPerformanceAdminDto.Response> updatePerformance(
            @PathVariable Long id,
            @RequestBody RefinedPerformanceAdminDto.UpdateRequest request
    ) {
        RefinedPerformance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFORMANCE_NOT_FOUND));

        // artist_name_kr 업데이트
        if (request.getArtistNameKr() != null) {
            performance.updateArtistNameKr(request.getArtistNameKr());
        }

        performanceRepository.save(performance);

        return ResponseEntity.ok(toResponse(performance));
    }

    /**
     * 공연 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformance(@PathVariable Long id) {
        if (!performanceRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorCode.PERFORMANCE_NOT_FOUND);
        }

        performanceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계
     */
    @GetMapping("/stats")
    public ResponseEntity<RefinedPerformanceAdminDto.Stats> getStats() {
        long total = performanceRepository.count();
        long withArtist = performanceRepository.countByArtistNameKrIsNotNull();
        long withoutArtist = performanceRepository.countByArtistNameKrIsNull();

        return ResponseEntity.ok(new RefinedPerformanceAdminDto.Stats(
                total, withArtist, withoutArtist
        ));
    }

    /**
     * Spotify 아티스트 정보 채우기
     */
    @PostMapping("/enrich-spotify")
    public ResponseEntity<SpotifyArtistManualMatchService.MatchResult> enrichSpotifyInfo() {
        SpotifyArtistManualMatchService.MatchResult result = spotifyArtistManualMatchService.searchAndMatchArtist();
        return ResponseEntity.ok(result);
    }


    /**
     * Entity → DTO 변환
     */
    private RefinedPerformanceAdminDto.Response toResponse(RefinedPerformance perf) {
        return RefinedPerformanceAdminDto.Response.builder()
                .id(perf.getId())
                .kopisId(perf.getKopisId())
                .title(perf.getTitle())
                .venue(perf.getVenue())
                .startDate(perf.getStartDate())
                .endDate(perf.getEndDate())
                .status(perf.getStatus())
                .genre(perf.getGenre())
                .artistNameKr(perf.getArtistNameKr())
                .artistNameEn(perf.getArtistNameEn())
                .artistGenres(perf.getArtistGenres())
                .posterUrl(perf.getPosterUrl())
                .build();
    }
}
