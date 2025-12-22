package com.stagelog.Stagelog.api.auth.controller;

import com.stagelog.Stagelog.api.auth.dto.LoginRequest;
import com.stagelog.Stagelog.api.auth.dto.SignupRequest;
import com.stagelog.Stagelog.api.auth.dto.TokenResponse;
import com.stagelog.Stagelog.api.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
            @Valid @RequestBody SignupRequest signupRequest) {
        authService.signUp(signupRequest.getUserId(), signupRequest.getPassword(), signupRequest.getEmail(), signupRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest loginRequest){
        String accessToken = authService.logIn(loginRequest.getUserId(),loginRequest.getPassword());
        TokenResponse tokenResponse = TokenResponse.toDto(accessToken);
        return ResponseEntity.ok(tokenResponse);
    }
}
