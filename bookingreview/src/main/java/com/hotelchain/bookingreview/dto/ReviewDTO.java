package com.hotelchain.bookingreview.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    private Long bookingId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}