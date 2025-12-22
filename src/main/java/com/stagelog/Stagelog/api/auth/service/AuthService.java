package com.stagelog.Stagelog.api.auth.service;

import com.stagelog.Stagelog.domain.User;
import com.stagelog.Stagelog.repository.UserRepository;
import com.stagelog.Stagelog.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(String userId, String password, String email, String username) {
        if (userRepository.existsByUserId(userId)) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        userRepository.findByUserId(userId)
                .ifPresent(user -> {
                    throw new RuntimeException("이미 존재하는 유저입니다.");
                });
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.save(User.create(userId, encodedPassword, email, username));
    }

    @Transactional
    public String logIn(String userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return jwtTokenProvider.createToken(user.getUserId(), user.getUsername());
    }
}
