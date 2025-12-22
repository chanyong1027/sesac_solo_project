package com.stagelog.Stagelog.spotify.controller;

import com.stagelog.Stagelog.spotify.service.SpotifyArtistSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/spotify")
@RequiredArgsConstructor
public class SpotifyAdminController {
    private final SpotifyArtistSearchService spotifyArtistSearchService;

    /**
     * 배치 검색 (안전)
     */
    @PostMapping("/search/batch")
    public ResponseEntity<SpotifyArtistSearchService.SearchResult> searchBatch(
            @RequestParam(defaultValue = "500") Integer batchSize
    ) {
        SpotifyArtistSearchService.SearchResult result =
                spotifyArtistSearchService.searchAllArtists(batchSize);

        return ResponseEntity.ok(result);
    }

    /**
     * 아티스트 전체 검색 (주의: 시간 오래 걸림)
     */
    @PostMapping("/search/all")
    public ResponseEntity<SpotifyArtistSearchService.SearchResult> searchAll() {
        SpotifyArtistSearchService.SearchResult result =
                spotifyArtistSearchService.searchAllArtists(null);

        return ResponseEntity.ok(result);
    }
}
