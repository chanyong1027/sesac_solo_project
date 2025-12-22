package com.stagelog.Stagelog.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequest {
    @NotBlank(message = "id는 필수입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    private LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public static LoginRequest toDto(String userId, String password) {
        return new LoginRequest(userId, password);
    }
}
