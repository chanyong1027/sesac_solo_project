package com.stagelog.Stagelog.global.security.service;

import com.stagelog.Stagelog.domain.User;
import com.stagelog.Stagelog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException{
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() ->  new UsernameNotFoundException("해당 id를 가진 유저가 없습니다 " + userId));
        return new CustomUserDetails(user);
    }
}
