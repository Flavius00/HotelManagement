package com.hotelchain.bookingreview.repository;

import com.hotelchain.bookingreview.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(Long userId);
    List<Review> findByRoomId(Long roomId);
    List<Review> findByRating(Integer rating);
    List<Review> findByRoomIdOrderByCreatedAtDesc(Long roomId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.roomId = :roomId")
    Double getAverageRatingByRoom(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.roomId = :roomId")
    Long countReviewsByRoom(@Param("roomId") Long roomId);

    @Query("SELECT r FROM Review r WHERE r.userId = :userId AND r.roomId = :roomId")
    List<Review> findByUserAndRoom(@Param("userId") Long userId, @Param("roomId") Long roomId);
}
