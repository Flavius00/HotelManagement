package com.hotelchain.bookingreview.service;

import com.hotelchain.bookingreview.dto.*;
import com.hotelchain.bookingreview.entity.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDTO createBooking(BookingCreateDTO bookingCreateDTO);
    BookingDTO updateBooking(Long id, BookingDTO bookingDTO);
    void cancelBooking(Long id);
    Optional<BookingDTO> getBookingById(Long id);
    List<BookingDTO> getAllBookings();
    List<BookingDTO> getBookingsByUser(Long userId);
    List<BookingDTO> getBookingsByRoom(Long roomId);
    List<BookingDTO> getBookingsByStatus(BookingStatus status);
    List<BookingDTO> getBookingsBetweenDates(LocalDate startDate, LocalDate endDate);
    boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);
    void confirmBooking(Long id);
}