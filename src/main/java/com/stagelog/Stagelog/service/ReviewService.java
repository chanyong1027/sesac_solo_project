package com.stagelog.Stagelog.service;

import com.stagelog.Stagelog.domain.Playlist;
import com.stagelog.Stagelog.domain.Review;
import com.stagelog.Stagelog.domain.Track;
import com.stagelog.Stagelog.domain.User;
import com.stagelog.Stagelog.dto.ReviewCreateRequest;
import com.stagelog.Stagelog.dto.ReviewDetailResponse;
import com.stagelog.Stagelog.dto.ReviewListResponse;
import com.stagelog.Stagelog.dto.ReviewUpdateRequest;
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

    public Long createReview(Long userId, ReviewCreateRequest request) { // 파라미터를 DTO로 받으면 더 깔끔합니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));

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
            return null; // 또는 정책에 따라 예외 발생
        }

        // playlistTitle이 없으면 기본값 사용
        String title = (playlistTitle != null && !playlistTitle.isEmpty())
                ? playlistTitle
                : "나의 플레이리스트";

        Playlist playlist = new Playlist(title);

        for (ReviewCreateRequest.TrackRequest t : trackRequests) {
            Track track = new Track(
                    t.getSpotifyId(),
                    t.getTitle(),
                    t.getArtistName(),
                    t.getAlbumImageUrl(),
                    t.getSpotifyUri(),
                    t.getExternalUrl(),
                    t.getDurationMs());
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        validateOwner(userId, review);
        reviewRepository.delete(review);
    }

    @Transactional
    public ReviewDetailResponse updateReview(Long userId, Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));
        validateOwner(userId, review);

        List<Track> newTracks = Optional.ofNullable(request.getTracks())
                .orElse(Collections.emptyList())
                .stream()
                .map(trackDto -> Track.builder()
                        .spotifyId(trackDto.getSpotifyId())
                        .title(trackDto.getTitle())
                        .artistName(trackDto.getArtistName())
                        .albumImageUrl(trackDto.getAlbumImageUrl())
                        .spotifyUri(trackDto.getSpotifyUri())
                        .externalUrl(trackDto.getExternalUrl())
                        .durationMs(trackDto.getDurationMs())
                        .build())
                .toList();
        review.update(request.getTitle(), request.getContent(), request.getPlaylistTitle(), newTracks);
        return ReviewDetailResponse.from(review);
    }

    @Transactional(readOnly = true)
    public ReviewDetailResponse getReviewDetail(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        validateOwner(userId, review);
        return ReviewDetailResponse.from(review);
    }

    private void validateOwner(Long userId, Review review) {
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
