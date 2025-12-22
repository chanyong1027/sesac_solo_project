package com.stagelog.Stagelog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterestedPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private RefinedPerformance performance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private InterestedPerformance(User user, RefinedPerformance performance) {
        this.user = user;
        this.performance = performance;
        this.createdAt = LocalDateTime.now();
    }

    public static InterestedPerformance create(User user, RefinedPerformance performance) {
        return new InterestedPerformance(user, performance);
    }
}
