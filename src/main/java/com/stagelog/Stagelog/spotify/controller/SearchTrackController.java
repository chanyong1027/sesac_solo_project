package com.stagelog.Stagelog.spotify.controller;

import com.stagelog.Stagelog.spotify.dto.SearchTrackResponse;
import com.stagelog.Stagelog.spotify.service.SearchTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
public class SearchTrackController {
    private final SearchTrackService searchTrackService;

    @GetMapping("/search/tracks")
    public ResponseEntity<SearchTrackResponse> searchTracks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "30") Integer limit
    ){
        SearchTrackResponse result =
                searchTrackService.searchTracks(keyword, limit);
        return ResponseEntity.ok(result);
    }
}
