package com.hotelchain.bookingreview.controller;

import com.hotelchain.bookingreview.dto.*;
import com.hotelchain.bookingreview.entity.BookingStatus;
import com.hotelchain.bookingreview.entity.FileFormat;
import com.hotelchain.bookingreview.service.BookingService;
import com.hotelchain.bookingreview.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;
    private final ExportService exportService;

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingCreateDTO bookingCreateDTO) {
        try {
            BookingDTO createdBooking = bookingService.createBooking(bookingCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (RuntimeException e) {
            log.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<BookingDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(booking -> ResponseEntity.ok(booking))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByUser(@PathVariable Long userId) {
        List<BookingDTO> bookings = bookingService.getBookingsByUser(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByRoom(@PathVariable Long roomId) {
        List<BookingDTO> bookings = bookingService.getBookingsByRoom(roomId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingDTO>> getBookingsByStatus(@PathVariable BookingStatus status) {
        List<BookingDTO> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/dates")
    public ResponseEntity<List<BookingDTO>> getBookingsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<BookingDTO> bookings = bookingService.getBookingsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/availability/{roomId}")
    public ResponseEntity<Boolean> checkRoomAvailability(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        boolean isAvailable = bookingService.isRoomAvailable(roomId, checkInDate, checkOutDate);
        return ResponseEntity.ok(isAvailable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long id,
                                                    @Valid @RequestBody BookingDTO bookingDTO) {
        try {
            BookingDTO updatedBooking = bookingService.updateBooking(id, bookingDTO);
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            log.error("Error updating booking: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error cancelling booking: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmBooking(@PathVariable Long id) {
        try {
            bookingService.confirmBooking(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error confirming booking: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportBookings(
            @RequestParam FileFormat format,
            @RequestParam(defaultValue = "bookings") String filename,
            @RequestBody(required = false) List<Long> bookingIds) {
        try {
            List<BookingDTO> bookings;
            if (bookingIds != null && !bookingIds.isEmpty()) {
                bookings = bookingIds.stream()
                        .map(bookingService::getBookingById)
                        .filter(opt -> opt.isPresent())
                        .map(opt -> opt.get())
                        .collect(java.util.stream.Collectors.toList());
            } else {
                bookings = bookingService.getAllBookings();
            }

            String exportedData = exportService.exportBookings(bookings, format, filename);
            return ResponseEntity.ok(exportedData);
        } catch (Exception e) {
            log.error("Error exporting bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}