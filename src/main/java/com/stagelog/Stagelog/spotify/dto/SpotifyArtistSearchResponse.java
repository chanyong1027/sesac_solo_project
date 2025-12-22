package com.stagelog.Stagelog.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyArtistSearchResponse {

    private Artists artists;

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Artists {
        private List<SpotifyArtist> items;
        private Integer total;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SpotifyArtist {

        private String id;
        private String name;
        private List<String> genres;
        private Integer popularity;
        private List<SpotifyImage> images;

        @JsonProperty("external_urls")
        private SpotifyExternalUrls externalUrls;

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class SpotifyImage {
            private String url;
            private Integer height;
            private Integer width;
        }

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class SpotifyExternalUrls {
            private String spotify;
        }
    }
}

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class SpotifyErrorResponse {
    private SpotifyError error;

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SpotifyError {
        private Integer status;
        private String message;
    }
}
