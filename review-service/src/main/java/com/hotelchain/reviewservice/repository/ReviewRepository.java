package com.hotelchain.reviewservice.repository;

import com.hotelchain.reviewservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Găsește review-urile pentru o cameră specifică (active)
    List<Review> findByRoomIdAndActiveTrueOrderByCreatedAtDesc(Long roomId);

    // Găsește toate review-urile pentru o cameră (inclusiv inactive)
    List<Review> findByRoomIdOrderByCreatedAtDesc(Long roomId);

    // Găsește review-urile pentru o cameră (active)
    List<Review> findByRoomIdAndActiveTrue(Long roomId);

    // Găsește review-urile pentru un client specific
    List<Review> findByClientIdAndActiveTrueOrderByCreatedAtDesc(Long clientId);

    // Găsește review-urile pentru o rezervare și client (pentru verificare duplicat)
    Optional<Review> findByReservationIdAndClientId(Long reservationId, Long clientId);

    // Găsește toate review-urile active
    List<Review> findByActiveTrueOrderByCreatedAtDesc();

    // Găsește toate review-urile active (fără sortare)
    List<Review> findByActiveTrue();

    // Găsește review-urile pentru un hotel prin lista de room IDs
    // Această metodă va fi folosită cu room IDs obținute din hotel service
    @Query("SELECT r FROM Review r WHERE r.roomId IN :roomIds " +
            "AND r.active = true ORDER BY r.createdAt DESC")
    List<Review> findByRoomIdsAndActiveTrueOrderByCreatedAtDesc(@Param("roomIds") List<Long> roomIds);

    // Statistici - numărul de review-uri din această lună
    @Query("SELECT COUNT(r) FROM Review r WHERE r.active = true " +
            "AND MONTH(r.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(r.createdAt) = YEAR(CURRENT_DATE)")
    long countReviewsThisMonth();

    // Statistici - rating mediu pentru o cameră
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.roomId = :roomId AND r.active = true")
    Double getAverageRatingForRoom(@Param("roomId") Long roomId);

    // Statistici - numărul de review-uri per rating
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.active = true GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistribution();

    // Găsește review-urile cu un anumit rating
    List<Review> findByRatingAndActiveTrueOrderByCreatedAtDesc(Integer rating);

    // Căutare în review-uri după text
    @Query("SELECT r FROM Review r WHERE r.active = true " +
            "AND (LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY r.createdAt DESC")
    List<Review> searchReviews(@Param("searchTerm") String searchTerm);

    // Top review-uri (cele mai bine cotate)
    List<Review> findByActiveTrueAndRatingGreaterThanEqualOrderByRatingDescCreatedAtDesc(Integer minRating);

    // Review-urile recente (din ultima săptămână) - FIXED VERSION
    @Query("SELECT r FROM Review r WHERE r.active = true " +
            "AND r.createdAt >= :weekAgo " +
            "ORDER BY r.createdAt DESC")
    List<Review> findRecentReviews(@Param("weekAgo") LocalDateTime weekAgo);

    // Alternative method using method name convention (Spring Data will generate the query)
    List<Review> findByActiveTrueAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(LocalDateTime weekAgo);
}