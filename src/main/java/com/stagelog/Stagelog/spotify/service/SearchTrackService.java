package com.stagelog.Stagelog.spotify.service;

import com.stagelog.Stagelog.spotify.client.SpotifyApiClient;
import com.stagelog.Stagelog.spotify.dto.SearchTrackResponse;
import com.stagelog.Stagelog.spotify.util.SpotifyRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchTrackService {
    private final SpotifyApiClient spotifyApiClient;
    private final SpotifyRateLimiter spotifyRateLimiter;

    @Transactional
    public SearchTrackResponse searchTracks(String keyword, Integer limit) {
        return spotifyApiClient.searchTrack(keyword, limit);
    }
}
