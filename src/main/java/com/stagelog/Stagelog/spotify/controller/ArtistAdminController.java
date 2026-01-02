package com.stagelog.Stagelog.spotify.controller;

import com.stagelog.Stagelog.domain.Artist;
import com.stagelog.Stagelog.global.exception.EntityNotFoundException;
import com.stagelog.Stagelog.global.exception.ErrorCode;
import com.stagelog.Stagelog.repository.ArtistRepository;
import com.stagelog.Stagelog.service.MigrationService;
import com.stagelog.Stagelog.spotify.dto.ArtistAdminDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/artists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 개발용
public class ArtistAdminController {

    private final ArtistRepository artistRepository;
    private final MigrationService  migrationService;

    /**
     * 상태별 아티스트 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ArtistAdminDto.Response>> getArtists(
            @RequestParam String status
    ) {
        List<Artist> artists = artistRepository.findBySearchStatus(status);

        List<ArtistAdminDto.Response> response = artists.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * name_kr 업데이트
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArtistAdminDto.Response> updateArtist(
            @PathVariable Long id,
            @RequestBody ArtistAdminDto.UpdateRequest request
    ) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ARTIST_NOT_FOUND));

        // (1) 한글 이름 업데이트 (입력값이 있을 때만)
        if (request.getNameKr() != null && !request.getNameKr().isBlank()) {
            artist.setNameKr(request.getNameKr());

            // (2) 상태 강제 변경: "이름 입력했으니 완료된 걸로 침"
            artist.setSearchStatus("COMPLETED");
        }

        artistRepository.save(artist);

        return ResponseEntity.ok(toResponse(artist));
    }

    /**
     * 통계
     */
    @GetMapping("/stats")
    public ResponseEntity<ArtistAdminDto.Stats> getStats() {
        long found = artistRepository.countBySearchStatus("FOUND");
        long needRetry = artistRepository.countBySearchStatus("NEED_RETRY");
        long notFound = artistRepository.countBySearchStatus("NOT_FOUND");
        long completed = artistRepository.countBySearchStatus("COMPLETED");

        return ResponseEntity.ok(new ArtistAdminDto.Stats(found, needRetry, notFound, completed));
    }

    private ArtistAdminDto.Response toResponse(Artist artist) {
        return ArtistAdminDto.Response.builder()
                .id(artist.getId())
                .name(artist.getName())
                .sampleSource(artist.getSourceTitle())
                .sampleKopisId(artist.getSourceKopisId())
                .nameKr(artist.getNameKr())
                .searchStatus(artist.getSearchStatus())
                .nameEn(artist.getNameEn())
                .spotifyPopularity(artist.getSpotifyPopularity())
                .spotifyGenres(artist.getSpotifyGenres())
                .build();
    }

    // refinedPerformnace에서 artist정보 분리하기 위한 api. 한 번 사용 용도 혹시 나중에 사용할지
    //몰라 주석처리

    /*@PostMapping("/run-all")
    public ResponseEntity<String> runFullMigration() {
        migrationService.migrateArtists();
        return ResponseEntity.ok("전체 마이그레이션이 시작되었습니다.");
    }*/

    @PostMapping("/match-and-migrate")
    public ResponseEntity<String> matchAndMigrate() {
        migrationService.migrateByArtistMatching();
        return ResponseEntity.ok("제목 기반 아티스트 매칭 및 정제 데이터 생성이 완료되었습니다.");
    }
}
