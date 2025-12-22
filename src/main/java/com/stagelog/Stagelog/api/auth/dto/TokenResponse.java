package com.stagelog.Stagelog.api.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenResponse {

    private String accessToken;
    private String tokenType;

    private TokenResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }

    public static TokenResponse toDto(String accessToken) {
        return new TokenResponse(accessToken);
    }

}
