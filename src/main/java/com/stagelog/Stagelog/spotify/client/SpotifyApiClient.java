package com.stagelog.Stagelog.spotify.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stagelog.Stagelog.spotify.dto.SearchTrackResponse;
import com.stagelog.Stagelog.spotify.dto.SpotifyArtistSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class SpotifyApiClient {
    private final RestClient restClient = RestClient.create();

    @Value("${external.spotify.client-id}")
    private String clientId;

    @Value("${external.spotify.client-secret}")
    private String clientSecret;

    private record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") Integer expiresIn
    ) {}

    private String accessToken;
    private Long tokenExpiresAt = 0L;

    public SpotifyArtistSearchResponse searchArtist(String artistName){
        try {
            ensureValidToken();

            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.spotify.com")
                            .path("/v1/search")
                            .queryParam("q", artistName)
                            .queryParam("type", "artist")
                            .queryParam("limit", 5)
                            .build())
                    .header("Authorization", "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 429) {
                            // 429 Rate Limit
                            String retryAfter = response.getHeaders().getFirst("Retry-After");
                            log.warn("Rate limit exceeded. Retry after: {} seconds", retryAfter);
                            throw new SpotifyRateLimitException("Rate limit exceeded", retryAfter);
                        } else {
                            log.error("Spotify API error: {}", response.getStatusCode());
                            throw new SpotifyApiException("API error: " + response.getStatusCode());
                        }
                    })
                    .body(SpotifyArtistSearchResponse.class);
        } catch (SpotifyRateLimitException | SpotifyApiException e){
            throw e;
        }catch (Exception e){
            log.error("Unexpected error calling Spotify API", e);
            throw new SpotifyApiException("Unexpected error: " + e.getMessage());
        }
    }

    public SearchTrackResponse searchTrack(String keyword, Integer limit) {
        try {
            ensureValidToken();

            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.spotify.com")
                            .path("/v1/search")
                            .queryParam("q", keyword)
                            .queryParam("type", "track")
                            .queryParam("limit", limit)
                            .build())
                    .header("Authorization", "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 429) {
                            // 429 Rate Limit 처리
                            String retryAfter = response.getHeaders().getFirst("Retry-After");
                            log.warn("Rate limit exceeded. Retry after: {} seconds", retryAfter);
                            throw new SpotifyRateLimitException("Rate limit exceeded", retryAfter);
                        } else {
                            log.error("Spotify API error: {}", response.getStatusCode());
                            throw new SpotifyApiException("API error: " + response.getStatusCode());
                        }
                    })
                    .body(SearchTrackResponse.class); // 결과를 SearchTrackResponse로 매핑

        } catch (SpotifyRateLimitException | SpotifyApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling Spotify API", e);
            throw new SpotifyApiException("Unexpected error: " + e.getMessage());
        }

    }
    private void ensureValidToken() {

        // 토큰이 유효하면 재사용
        if (accessToken != null && System.currentTimeMillis() < tokenExpiresAt) {
            return;
        }

        log.info("Getting new Spotify access token...");

        try {
            // Basic Auth 헤더
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            TokenResponse response = restClient.post()
                    .uri("https://accounts.spotify.com/api/token")
                    .header("Authorization", "Basic " + encodedAuth)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body("grant_type=client_credentials")
                    .retrieve()
                    .body(TokenResponse.class);  // ✅ 경고 없음

            this.accessToken = response.accessToken();
            Integer expiresIn = response.expiresIn();

            // 만료 시간 (여유있게 5분 일찍)
            this.tokenExpiresAt = System.currentTimeMillis() + ((expiresIn - 300) * 1000L);

            log.info("✅ Spotify access token obtained. Expires in {} seconds", expiresIn);

        } catch (Exception e) {
            log.error("Failed to get Spotify access token", e);
            throw new SpotifyApiException("Failed to get access token: " + e.getMessage());
        }
    }


    /**
     * Rate Limit 예외
     */
    public static class SpotifyRateLimitException extends RuntimeException {
        private final String retryAfter;

        public SpotifyRateLimitException(String message, String retryAfter) {
            super(message);
            this.retryAfter = retryAfter;
        }

        public int getRetryAfterSeconds() {
            return retryAfter != null ? Integer.parseInt(retryAfter) : 30;
        }
    }

    /**
     * API 예외
     */
    public static class SpotifyApiException extends RuntimeException {
        public SpotifyApiException(String message) {
            super(message);
        }
    }
}
