package com.hotelchain.hotelservice.repository;

import com.hotelchain.hotelservice.entity.Room;
import com.hotelchain.hotelservice.entity.RoomType;
import com.hotelchain.hotelservice.entity.RoomPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // Găsește camerele pentru un hotel specific
    List<Room> findByHotelIdAndActiveTrue(Long hotelId);

    // Găsește camere disponibile
    List<Room> findByAvailableTrueAndActiveTrue();

    // Găsește camere după tip
    List<Room> findByRoomTypeAndActiveTrue(RoomType roomType);

    // Găsește camere după poziție
    List<Room> findByPositionAndActiveTrue(RoomPosition position);

    // Găsește camere într-o anumită zonă de preț
    List<Room> findByPricePerNightBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

    // Query complex pentru căutare cu filtre
    @Query("SELECT DISTINCT r FROM Room r " +
            "JOIN r.hotel h " +
            "LEFT JOIN r.facilities f " +
            "WHERE r.active = true " +
            "AND (:location IS NULL OR LOWER(h.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:available IS NULL OR r.available = :available) " +
            "AND (:minPrice IS NULL OR r.pricePerNight >= :minPrice) " +
            "AND (:maxPrice IS NULL OR r.pricePerNight <= :maxPrice) " +
            "AND (:position IS NULL OR r.position = :position) " +
            "AND (:roomType IS NULL OR r.roomType = :roomType) " +
            "AND (:minGuests IS NULL OR r.maxGuests >= :minGuests)")
    List<Room> findRoomsWithCriteria(@Param("location") String location,
                                     @Param("available") Boolean available,
                                     @Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice,
                                     @Param("position") RoomPosition position,
                                     @Param("roomType") RoomType roomType,
                                     @Param("minGuests") Integer minGuests);

    // Găsește camere cu o anumită facilitate
    @Query("SELECT DISTINCT r FROM Room r " +
            "JOIN r.facilities f " +
            "WHERE r.active = true AND f.facility = :facility")
    List<Room> findByFacility(@Param("facility") String facility);

    // Sortare după locație și numărul camerei
    @Query("SELECT r FROM Room r " +
            "JOIN r.hotel h " +
            "WHERE r.active = true " +
            "ORDER BY h.location ASC, r.roomNumber ASC")
    List<Room> findAllSortedByLocationAndRoomNumber();
}