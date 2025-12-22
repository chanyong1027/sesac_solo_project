package com.stagelog.Stagelog.global.security.service;

import com.stagelog.Stagelog.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = "ROLE_USER";
        return Collections.singleton(new SimpleGrantedAuthority(roleName));
    }

    // 2. 비밀번호 리턴
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 3. 아이디(고유 식별자) 리턴 -> 우리는 이메일을 아이디로 씁니다.
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // 4. 계정 상태 관리 (복잡한 로직이 없다면 모두 true로 설정)
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 안됨
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비번 만료 안됨
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화됨
    }
}
