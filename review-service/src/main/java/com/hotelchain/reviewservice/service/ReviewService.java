package com.hotelchain.reviewservice.service;

import com.hotelchain.reviewservice.dto.*;
import com.hotelchain.reviewservice.entity.Review;
import com.hotelchain.reviewservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private JwtValidationService jwtValidationService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.services.user:http://localhost:8081}")
    private String userServiceUrl;

    @Value("${app.services.hotel:http://localhost:8082}")
    private String hotelServiceUrl;

    @Value("${app.services.reservation:http://localhost:8083}")
    private String reservationServiceUrl;

    // PUBLIC METHODS - fără autentificare

    /**
     * Obține toate review-urile pentru o cameră
     */
    public List<ReviewDto> getReviewsForRoom(Long roomId) {
        List<Review> reviews = reviewRepository.findByRoomIdAndActiveTrueOrderByCreatedAtDesc(roomId);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obține toate review-urile pentru un hotel
     */
    public List<ReviewDto> getReviewsForHotel(Long hotelId) {
        List<Review> reviews = reviewRepository.findByHotelIdAndActiveTrueOrderByCreatedAtDesc(hotelId);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obține statistici review-uri pentru o cameră
     */
    public Map<String, Object> getRoomReviewStats(Long roomId) {
        List<Review> reviews = reviewRepository.findByRoomIdAndActiveTrue(roomId);

        if (reviews.isEmpty()) {
            return Map.of(
                    "totalReviews", 0,
                    "averageRating", 0.0,
                    "ratingDistribution", Map.of()
            );
        }

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Map<Integer, Long> ratingDistribution = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

        return Map.of(
                "totalReviews", reviews.size(),
                "averageRating", Math.round(averageRating * 100.0) / 100.0,
                "ratingDistribution", ratingDistribution
        );
    }

    // CLIENT METHODS - cu autentificare ca CLIENT

    /**
     * Adaugă un review pentru o cameră rezervată (doar CLIENT)
     */
    public ReviewDto addReview(CreateReviewRequest request, String token) {
        // Validează că utilizatorul este CLIENT
        jwtValidationService.validateClientRole(token);
        Long clientId = jwtValidationService.getUserIdFromToken(token);

        // Verifică că review-ul este pentru o rezervare validă a clientului
        if (!isValidReservationForClient(request.getReservationId(), clientId)) {
            throw new RuntimeException("You can only review rooms from your completed reservations");
        }

        // Verifică că clientul nu a dat deja review pentru această rezervare
        if (reviewRepository.findByReservationIdAndClientId(request.getReservationId(), clientId).isPresent()) {
            throw new RuntimeException("You have already reviewed this reservation");
        }

        // Validează rating-ul
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setRoomId(request.getRoomId());
        review.setClientId(clientId);
        review.setReservationId(request.getReservationId());
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        return convertToDto(review);
    }

    /**
     * Obține review-urile unui client
     */
    public List<ReviewDto> getClientReviews(String token) {
        jwtValidationService.validateClientRole(token);
        Long clientId = jwtValidationService.getUserIdFromToken(token);

        List<Review> reviews = reviewRepository.findByClientIdAndActiveTrueOrderByCreatedAtDesc(clientId);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Actualizează un review (doar al clientului)
     */
    public ReviewDto updateReview(Long reviewId, CreateReviewRequest request, String token) {
        jwtValidationService.validateClientRole(token);
        Long clientId = jwtValidationService.getUserIdFromToken(token);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Verifică că review-ul aparține clientului
        if (!review.getClientId().equals(clientId)) {
            throw new RuntimeException("You can only update your own reviews");
        }

        // Validează rating-ul
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        return convertToDto(review);
    }

    /**
     * Șterge un review (doar al clientului)
     */
    public void deleteReview(Long reviewId, String token) {
        jwtValidationService.validateClientRole(token);
        Long clientId = jwtValidationService.getUserIdFromToken(token);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Verifică că review-ul aparține clientului
        if (!review.getClientId().equals(clientId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        // Soft delete
        review.setActive(false);
        reviewRepository.save(review);
    }

    // MANAGER METHODS - cu autentificare ca MANAGER

    /**
     * Obține toate review-urile (pentru manageri)
     */
    public List<ReviewDto> getAllReviews(String token) {
        jwtValidationService.validateManagerRole(token);

        List<Review> reviews = reviewRepository.findByActiveTrueOrderByCreatedAtDesc();
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obține statistici generale despre review-uri (pentru manageri)
     */
    public Map<String, Object> getReviewStatistics(String token) {
        jwtValidationService.validateManagerRole(token);

        List<Review> allReviews = reviewRepository.findByActiveTrue();

        if (allReviews.isEmpty()) {
            return Map.of(
                    "totalReviews", 0,
                    "averageRating", 0.0,
                    "ratingDistribution", Map.of(),
                    "reviewsThisMonth", 0
            );
        }

        double averageRating = allReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Map<Integer, Long> ratingDistribution = allReviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

        long reviewsThisMonth = reviewRepository.countReviewsThisMonth();

        return Map.of(
                "totalReviews", allReviews.size(),
                "averageRating", Math.round(averageRating * 100.0) / 100.0,
                "ratingDistribution", ratingDistribution,
                "reviewsThisMonth", reviewsThisMonth
        );
    }

    /**
     * Moderează un review (ascunde/afișează)
     */
    public ReviewDto moderateReview(Long reviewId, boolean active, String token) {
        jwtValidationService.validateManagerRole(token);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setActive(active);
        review = reviewRepository.save(review);

        return convertToDto(review);
    }

    // HELPER METHODS

    private boolean isValidReservationForClient(Long reservationId, Long clientId) {
        try {
            String url = reservationServiceUrl + "/api/reservations/" + reservationId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> reservation = response.getBody();
                Long reservationClientId = Long.valueOf(reservation.get("clientId").toString());
                String status = (String) reservation.get("status");

                // Verifică că rezervarea aparține clientului și este completată
                return reservationClientId.equals(clientId) &&
                        ("CHECKED_OUT".equals(status) || "COMPLETED".equals(status));
            }
        } catch (Exception e) {
            System.err.println("Failed to validate reservation: " + e.getMessage());
        }
        return false;
    }

    private String getClientName(Long clientId) {
        try {
            String url = userServiceUrl + "/api/users/" + clientId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("username");
            }
        } catch (Exception e) {
            System.err.println("Failed to get client name: " + e.getMessage());
        }
        return "Anonymous";
    }

    private Map<String, Object> getRoomInfo(Long roomId) {
        try {
            String url = hotelServiceUrl + "/api/hotels/rooms/" + roomId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> room = response.getBody();
                return Map.of(
                        "roomNumber", room.get("roomNumber"),
                        "hotelName", room.get("hotelName")
                );
            }
        } catch (Exception e) {
            System.err.println("Failed to get room info: " + e.getMessage());
        }
        return Map.of(
                "roomNumber", "Room #" + roomId,
                "hotelName", "Unknown Hotel"
        );
    }

    private ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRoomId(review.getRoomId());
        dto.setClientId(review.getClientId());
        dto.setReservationId(review.getReservationId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setActive(review.isActive());

        // Încarcă informații suplimentare
        try {
            dto.setClientName(getClientName(review.getClientId()));
            Map<String, Object> roomInfo = getRoomInfo(review.getRoomId());
            dto.setRoomNumber((String) roomInfo.get("roomNumber"));
            dto.setHotelName((String) roomInfo.get("hotelName"));
        } catch (Exception e) {
            // Log but don't fail
            System.err.println("Failed to load additional review info: " + e.getMessage());
            dto.setClientName("Anonymous");
            dto.setRoomNumber("Room #" + review.getRoomId());
            dto.setHotelName("Unknown Hotel");
        }

        return dto;
    }
}