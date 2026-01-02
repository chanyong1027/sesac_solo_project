package com.stagelog.Stagelog.api.auth.service;

import com.stagelog.Stagelog.domain.User;
import com.stagelog.Stagelog.global.exception.DuplicateEntityException;
import com.stagelog.Stagelog.global.exception.EntityNotFoundException;
import com.stagelog.Stagelog.global.exception.ErrorCode;
import com.stagelog.Stagelog.global.exception.UnauthorizedException;
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
            throw new DuplicateEntityException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(password);
        userRepository.save(User.create(userId, encodedPassword, email, username));
    }

    @Transactional(readOnly = true)
    public String logIn(String userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
        }
        return jwtTokenProvider.createToken(user.getUserId(), user.getUsername());
    }
}
