package com.hotelchain.roommanagement.repository;

import com.hotelchain.roommanagement.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByLocationContainingIgnoreCase(String location);
    List<Hotel> findByNameContainingIgnoreCase(String name);
    List<Hotel> findByStarRating(Integer starRating);

    @Query("SELECT h FROM Hotel h WHERE h.starRating >= :minRating")
    List<Hotel> findByMinStarRating(@Param("minRating") Integer minRating);
}