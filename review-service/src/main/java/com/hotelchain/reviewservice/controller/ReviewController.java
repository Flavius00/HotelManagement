package com.hotelchain.reviewservice.controller;

import com.hotelchain.reviewservice.dto.*;
import com.hotelchain.reviewservice.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Test endpoints
    @GetMapping("/test")
    public String test() {
        return "Review Service is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"UP\",\"service\":\"review-service\"}";
    }

    // PUBLIC ENDPOINTS - fără autentificare

    /**
     * Obține review-urile pentru o cameră
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ReviewDto>> getReviewsForRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(reviewService.getReviewsForRoom(roomId));
    }

    /**
     * Obține review-urile pentru un hotel
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ReviewDto>> getReviewsForHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getReviewsForHotel(hotelId));
    }

    /**
     * Obține statistici review-uri pentru o cameră
     */
    @GetMapping("/room/{roomId}/stats")
    public ResponseEntity<Map<String, Object>> getRoomReviewStats(@PathVariable Long roomId) {
        return ResponseEntity.ok(reviewService.getRoomReviewStats(roomId));
    }

    // CLIENT ENDPOINTS - cu autentificare

    /**
     * Adaugă un review nou (doar CLIENT)
     */
    @PostMapping
    public ResponseEntity<ReviewDto> addReview(@RequestBody CreateReviewRequest request,
                                               @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reviewService.addReview(request, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Obține review-urile clientului curent
     */
    @GetMapping("/my-reviews")
    public ResponseEntity<List<ReviewDto>> getMyReviews(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reviewService.getClientReviews(token));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * Actualizează un review (doar al clientului)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long id,
                                                  @RequestBody CreateReviewRequest request,
                                                  @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reviewService.updateReview(id, request, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Șterge un review (doar al clientului)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id,
                                             @RequestHeader("Authorization") String token) {
        try {
            reviewService.deleteReview(id, token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // MANAGER ENDPOINTS - cu autentificare ca MANAGER

    /**
     * Obține toate review-urile (pentru manageri)
     */
    @GetMapping("/all")
    public ResponseEntity<List<ReviewDto>> getAllReviews(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reviewService.getAllReviews(token));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * Obține statistici generale despre review-uri
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getReviewStatistics(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reviewService.getReviewStatistics(token));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * Moderează un review (ascunde/afișează)
     */
    @PutMapping("/{id}/moderate")
    public ResponseEntity<ReviewDto> moderateReview(@PathVariable Long id,
                                                    @RequestParam boolean active,
                                                    @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reviewService.moderateReview(id, active, token));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
        }
    }
}