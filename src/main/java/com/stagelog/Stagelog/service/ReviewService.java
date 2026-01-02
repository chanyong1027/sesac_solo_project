package com.stagelog.Stagelog.service;

import com.stagelog.Stagelog.domain.Playlist;
import com.stagelog.Stagelog.domain.Review;
import com.stagelog.Stagelog.domain.Track;
import com.stagelog.Stagelog.domain.User;
import com.stagelog.Stagelog.dto.ReviewCreateRequest;
import com.stagelog.Stagelog.dto.ReviewDetailResponse;
import com.stagelog.Stagelog.dto.ReviewListResponse;
import com.stagelog.Stagelog.dto.ReviewUpdateRequest;
import com.stagelog.Stagelog.global.exception.EntityNotFoundException;
import com.stagelog.Stagelog.global.exception.ErrorCode;
import com.stagelog.Stagelog.global.exception.UnauthorizedException;
import com.stagelog.Stagelog.repository.ReviewRepository;
import com.stagelog.Stagelog.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public Long createReview(Long userId, ReviewCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        Playlist playlist = createPlaylist(request.getPlaylistTitle(), request.getTracks());

        Review review = Review.create(
                user,
                request.getTitle(),
                request.getContent(),
                playlist);

        return reviewRepository.save(review).getId();
    }

    private Playlist createPlaylist(String playlistTitle, List<ReviewCreateRequest.TrackRequest> trackRequests) {
        if (trackRequests == null || trackRequests.isEmpty()) {
            return null;
        }

        // playlistTitle이 없으면 기본값 사용
        String title = (playlistTitle != null && !playlistTitle.isEmpty())
                ? playlistTitle
                : "나의 플레이리스트";

        Playlist playlist = new Playlist(title);

        // 정적 팩토리 메서드 사용
        for (ReviewCreateRequest.TrackRequest trackRequest : trackRequests) {
            Track track = Track.from(trackRequest);
            playlist.addTrack(track);
        }
        return playlist;
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponse> getMyReviews(Long userId) {

        List<Review> reviews = reviewRepository.findByUserId(userId);

        return reviews.stream()
                .map(ReviewListResponse::from)
                .toList();
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        validateOwner(userId, review);
        reviewRepository.delete(review);
    }

    @Transactional
    public ReviewDetailResponse updateReview(Long userId, Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
        validateOwner(userId, review);

        // 정적 팩토리 메서드 사용
        List<Track> newTracks = Optional.ofNullable(request.getTracks())
                .orElse(Collections.emptyList())
                .stream()
                .map(Track::from)
                .toList();

        review.update(request.getTitle(), request.getContent(), request.getPlaylistTitle(), newTracks);
        return ReviewDetailResponse.from(review);
    }

    @Transactional(readOnly = true)
    public ReviewDetailResponse getReviewDetail(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        validateOwner(userId, review);
        return ReviewDetailResponse.from(review);
    }

    private void validateOwner(Long userId, Review review) {
        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedException(ErrorCode.REVIEW_ACCESS_DENIED);
        }
    }
}
