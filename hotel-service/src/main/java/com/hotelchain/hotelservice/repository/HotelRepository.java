package com.hotelchain.hotelservice.repository;

import com.hotelchain.hotelservice.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // Găsește hotelurile active
    List<Hotel> findByActiveTrue();

    // Găsește hotelurile după locație
    List<Hotel> findByLocationContainingIgnoreCaseAndActiveTrue(String location);

    // Găsește hotelurile după numărul de stele
    List<Hotel> findByStarRatingAndActiveTrue(Integer starRating);

    // Găsește hotelurile după nume
    List<Hotel> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    // Query pentru căutare cu criterii multiple
    @Query("SELECT h FROM Hotel h WHERE h.active = true " +
            "AND (:location IS NULL OR LOWER(h.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:minStars IS NULL OR h.starRating >= :minStars) " +
            "AND (:maxStars IS NULL OR h.starRating <= :maxStars)")
    List<Hotel> findHotelsWithCriteria(@Param("location") String location,
                                       @Param("name") String name,
                                       @Param("minStars") Integer minStars,
                                       @Param("maxStars") Integer maxStars);
}