package com.hotelchain.reservationservice.repository;

import com.hotelchain.reservationservice.entity.Reservation;
import com.hotelchain.reservationservice.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Găsește rezervările pentru un client specific
    List<Reservation> findByClientIdOrderByCreatedAtDesc(Long clientId);

    // Găsește rezervările pentru o cameră specifică
    List<Reservation> findByRoomIdOrderByCheckInDateDesc(Long roomId);

    // Găsește rezervările după status
    List<Reservation> findByStatus(ReservationStatus status);

    // Găsește rezervările pentru un angajat specific
    List<Reservation> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

    // Găsește rezervările care se suprapun pentru o cameră (pentru verificarea disponibilității)
    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId " +
            "AND r.status NOT IN ('CANCELLED') " +
            "AND ((r.checkInDate <= :checkOut AND r.checkOutDate >= :checkIn))")
    List<Reservation> findConflictingReservations(@Param("roomId") Long roomId,
                                                  @Param("checkIn") LocalDate checkIn,
                                                  @Param("checkOut") LocalDate checkOut);

    // REMOVED: Cross-service query that was causing the error
    // This should be handled in the service layer by calling the user-service
    // List<Reservation> findByEmployeeHotelId(@Param("hotelId") Long hotelId);

    // Găsește rezervările dintr-o perioadă specifică
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate >= :startDate AND r.checkInDate <= :endDate")
    List<Reservation> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Găsește rezervările active (checked-in)
    List<Reservation> findByStatusAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            ReservationStatus status, LocalDate today1, LocalDate today2);

    // Statistici - numărul de rezervări per lună
    @Query("SELECT MONTH(r.checkInDate) as month, COUNT(r) as count " +
            "FROM Reservation r " +
            "WHERE YEAR(r.checkInDate) = :year " +
            "GROUP BY MONTH(r.checkInDate) ")
    List<Object[]> getReservationCountByMonth(@Param("year") int year);

    // Statistici - venitul total per lună
    @Query("SELECT MONTH(r.checkInDate) as month, SUM(r.totalPrice) as revenue " +
            "FROM Reservation r " +
            "WHERE YEAR(r.checkInDate) = :year AND r.status != 'CANCELLED' " +
            "GROUP BY MONTH(r.checkInDate) ")
    List<Object[]> getRevenueByMonth(@Param("year") int year);
}