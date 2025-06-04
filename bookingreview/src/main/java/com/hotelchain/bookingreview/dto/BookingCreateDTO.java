package com.hotelchain.bookingreview.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreateDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date cannot be in the past")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    private String specialRequests;
    private Long createdBy;
}