package com.stagelog.Stagelog.controller;

import com.stagelog.Stagelog.dto.ReviewCreateRequest;
import com.stagelog.Stagelog.dto.ReviewDetailResponse;
import com.stagelog.Stagelog.dto.ReviewListResponse;
import com.stagelog.Stagelog.dto.ReviewUpdateRequest;
import com.stagelog.Stagelog.global.security.service.CustomUserDetails;
import com.stagelog.Stagelog.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Long> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReviewCreateRequest request) {
        Long userId = userDetails.getUser().getId();
        Long reviewId = reviewService.createReview(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewId);
    }

    @GetMapping
    public ResponseEntity<List<ReviewListResponse>> getAllReviews(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();

        return ResponseEntity.ok(reviewService.getMyReviews(userId));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDetailResponse> getReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {

        Long userId = userDetails.getUser().getId();

        ReviewDetailResponse response = reviewService.getReviewDetail(userId, reviewId);

        return ResponseEntity.ok(response);

    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDetailResponse> updateReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request
    ){
        Long userId = userDetails.getUser().getId();

        ReviewDetailResponse response = reviewService.updateReview(userId, reviewId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {
        Long userId = userDetails.getUser().getId();

        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
