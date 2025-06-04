package com.hotelchain.bookingreview.service.impl;

import com.hotelchain.bookingreview.dto.ReviewDTO;
import com.hotelchain.bookingreview.entity.Review;
import com.hotelchain.bookingreview.repository.ReviewRepository;
import com.hotelchain.bookingreview.service.ReviewService;
import com.hotelchain.bookingreview.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        log.info("Creating new review for room: {} by user: {}",
                reviewDTO.getRoomId(), reviewDTO.getUserId());

        Review review = reviewMapper.toEntity(reviewDTO);
        Review savedReview = reviewRepository.save(review);

        log.info("Review created successfully with ID: {}", savedReview.getId());
        return reviewMapper.toDTO(savedReview);
    }

    @Override
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {
        log.info("Updating review with ID: {}", id);

        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        existingReview.setRating(reviewDTO.getRating());
        existingReview.setComment(reviewDTO.getComment());

        Review updatedReview = reviewRepository.save(existingReview);
        log.info("Review updated successfully with ID: {}", updatedReview.getId());

        return reviewMapper.toDTO(updatedReview);
    }

    @Override
    public void deleteReview(Long id) {
        log.info("Deleting review with ID: {}", id);

        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found");
        }

        reviewRepository.deleteById(id);
        log.info("Review deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReviewDTO> getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByRoom(Long roomId) {
        return reviewRepository.findByRoomIdOrderByCreatedAtDesc(roomId).stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByRoom(Long roomId) {
        return reviewRepository.getAverageRatingByRoom(roomId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReviewsByRoom(Long roomId) {
        return reviewRepository.countReviewsByRoom(roomId);
    }
}