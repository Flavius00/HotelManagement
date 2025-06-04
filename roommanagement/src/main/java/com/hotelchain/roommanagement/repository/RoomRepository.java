package com.hotelchain.roommanagement.repository;

import com.hotelchain.roommanagement.entity.Room;
import com.hotelchain.roommanagement.entity.RoomType;
import com.hotelchain.roommanagement.entity.RoomPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);
    List<Room> findByIsAvailable(Boolean isAvailable);
    List<Room> findByRoomType(RoomType roomType);
    List<Room> findByPosition(RoomPosition position);
    List<Room> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT r FROM Room r WHERE r.hotel.location LIKE %:location% ORDER BY r.hotel.location, r.roomNumber")
    List<Room> findByLocationSorted(@Param("location") String location);

    @Query("SELECT r FROM Room r JOIN r.hotel h WHERE " +
            "(:location IS NULL OR h.location LIKE %:location%) AND " +
            "(:isAvailable IS NULL OR r.isAvailable = :isAvailable) AND " +
            "(:minPrice IS NULL OR r.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR r.price <= :maxPrice) AND " +
            "(:position IS NULL OR r.position = :position) AND " +
            "(:roomType IS NULL OR r.roomType = :roomType)")
    List<Room> findRoomsWithFilters(
            @Param("location") String location,
            @Param("isAvailable") Boolean isAvailable,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("position") RoomPosition position,
            @Param("roomType") RoomType roomType
    );

    @Query("SELECT r FROM Room r JOIN r.facilities f WHERE f.facilityName IN :facilities")
    List<Room> findByFacilities(@Param("facilities") List<String> facilities);
}