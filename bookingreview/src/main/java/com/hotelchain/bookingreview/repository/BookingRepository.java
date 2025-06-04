package com.hotelchain.bookingreview.repository;

import com.hotelchain.bookingreview.entity.Booking;
import com.hotelchain.bookingreview.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByRoomId(Long roomId);
    List<Booking> findByBookingStatus(BookingStatus status);
    List<Booking> findByCreatedBy(Long createdBy);

    @Query("SELECT b FROM Booking b WHERE b.checkInDate >= :startDate AND b.checkOutDate <= :endDate")
    List<Booking> findBookingsBetweenDates(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE b.roomId = :roomId AND " +
            "((b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate)) AND " +
            "b.bookingStatus IN ('CONFIRMED', 'PENDING')")
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId,
                                          @Param("checkInDate") LocalDate checkInDate,
                                          @Param("checkOutDate") LocalDate checkOutDate);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.roomId = :roomId AND b.bookingStatus = 'CONFIRMED'")
    Long countConfirmedBookingsByRoom(@Param("roomId") Long roomId);
}