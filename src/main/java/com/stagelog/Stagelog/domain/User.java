package com.stagelog.Stagelog.domain;

import com.stagelog.Stagelog.global.exception.ErrorCode;
import com.stagelog.Stagelog.global.exception.InvalidInputException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
            throw new InvalidInputException(ErrorCode.INVALID_USER_ID);
        }
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidInputException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
    }
}
