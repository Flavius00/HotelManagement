package com.hotelchain.bookingreview.service;

import com.hotelchain.bookingreview.dto.ReviewDTO;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO);
    ReviewDTO updateReview(Long id, ReviewDTO reviewDTO);
    void deleteReview(Long id);
    Optional<ReviewDTO> getReviewById(Long id);
    List<ReviewDTO> getAllReviews();
    List<ReviewDTO> getReviewsByUser(Long userId);
    List<ReviewDTO> getReviewsByRoom(Long roomId);
    Double getAverageRatingByRoom(Long roomId);
    Long countReviewsByRoom(Long roomId);
}