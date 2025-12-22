package com.stagelog.Stagelog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterestedPerformance> interestedPerformances = new ArrayList<>();


    private User (String userId, String password, String email, String username,  Role role) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.username = username;
        this.role = role;
    }

    public static User create(String userId, String password, String email, String username) {
        validateLoginId(userId);
        validateEmail(email);
        return new User(userId, password, email, username,Role.USER);
    }

    private static void validateLoginId(String loginId) {
        if (loginId == null || loginId.isEmpty()) {
            throw new IllegalArgumentException("로그인 id는 필수입니다.");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("올바르지 않은 이메일 형식입니다.");
        }
    }
}
