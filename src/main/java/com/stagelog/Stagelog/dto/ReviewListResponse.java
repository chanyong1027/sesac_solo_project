package com.stagelog.Stagelog.dto;

import com.stagelog.Stagelog.domain.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewListResponse {
    private Long id;
    private String title;
    private LocalDateTime createdAt;

    public static ReviewListResponse from(Review review) {
        return new ReviewListResponse(
                review.getId(),
                review.getTitle(),
                review.getCreatedAt()
        );
    }
}
