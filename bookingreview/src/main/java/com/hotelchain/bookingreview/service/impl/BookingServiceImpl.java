package com.hotelchain.bookingreview.service.impl;

import com.hotelchain.bookingreview.dto.*;
import com.hotelchain.bookingreview.entity.Booking;
import com.hotelchain.bookingreview.entity.BookingStatus;
import com.hotelchain.bookingreview.repository.BookingRepository;
import com.hotelchain.bookingreview.service.BookingService;
import com.hotelchain.bookingreview.mapper.BookingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDTO createBooking(BookingCreateDTO bookingCreateDTO) {
        log.info("Creating new booking for user: {} and room: {}",
                bookingCreateDTO.getUserId(), bookingCreateDTO.getRoomId());

        // Check room availability
        if (!isRoomAvailable(bookingCreateDTO.getRoomId(),
                bookingCreateDTO.getCheckInDate(),
                bookingCreateDTO.getCheckOutDate())) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        // Calculate total price (simplified - in real scenario, fetch room price)
        long days = ChronoUnit.DAYS.between(bookingCreateDTO.getCheckInDate(),
                bookingCreateDTO.getCheckOutDate());
        BigDecimal totalPrice = BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(days)); // Mock price

        Booking booking = Booking.builder()
                .userId(bookingCreateDTO.getUserId())
                .roomId(bookingCreateDTO.getRoomId())
                .checkInDate(bookingCreateDTO.getCheckInDate())
                .checkOutDate(bookingCreateDTO.getCheckOutDate())
                .totalPrice(totalPrice)
                .specialRequests(bookingCreateDTO.getSpecialRequests())
                .createdBy(bookingCreateDTO.getCreatedBy())
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {}", savedBooking.getId());

        return bookingMapper.toDTO(savedBooking);
    }

    @Override
    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        log.info("Updating booking with ID: {}", id);

        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        existingBooking.setCheckInDate(bookingDTO.getCheckInDate());
        existingBooking.setCheckOutDate(bookingDTO.getCheckOutDate());
        existingBooking.setTotalPrice(bookingDTO.getTotalPrice());
        existingBooking.setSpecialRequests(bookingDTO.getSpecialRequests());
        existingBooking.setBookingStatus(bookingDTO.getBookingStatus());

        Booking updatedBooking = bookingRepository.save(existingBooking);
        log.info("Booking updated successfully with ID: {}", updatedBooking.getId());

        return bookingMapper.toDTO(updatedBooking);
    }

    @Override
    public void cancelBooking(Long id) {
        log.info("Cancelling booking with ID: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        log.info("Booking cancelled successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookingDTO> getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByRoom(Long roomId) {
        return bookingRepository.findByRoomId(roomId).stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByBookingStatus(status).stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findBookingsBetweenDates(startDate, endDate).stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                roomId, checkInDate, checkOutDate);
        return conflictingBookings.isEmpty();
    }

    @Override
    public void confirmBooking(Long id) {
        log.info("Confirming booking with ID: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        log.info("Booking confirmed successfully with ID: {}", id);
    }
}
