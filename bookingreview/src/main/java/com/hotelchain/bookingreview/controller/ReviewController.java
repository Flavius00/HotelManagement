package com.hotelchain.bookingreview.controller;

import com.hotelchain.bookingreview.dto.ReviewDTO;
import com.hotelchain.bookingreview.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        try {
            ReviewDTO createdReview = reviewService.createReview(reviewDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (RuntimeException e) {
            log.error("Error creating review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(review -> ResponseEntity.ok(review))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByRoom(@PathVariable Long roomId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByRoom(roomId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long userId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/room/{roomId}/average")
    public ResponseEntity<Double> getAverageRatingByRoom(@PathVariable Long roomId) {
        Double averageRating = reviewService.getAverageRatingByRoom(roomId);
        return ResponseEntity.ok(averageRating);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id,
                                                  @Valid @RequestBody ReviewDTO reviewDTO) {
        try {
            ReviewDTO updatedReview = reviewService.updateReview(id, reviewDTO);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            log.error("Error updating review: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting review: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}