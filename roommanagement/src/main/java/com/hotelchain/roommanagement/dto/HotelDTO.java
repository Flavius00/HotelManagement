package com.hotelchain.roommanagement.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDTO {
    private Long id;

    @NotBlank(message = "Hotel name is required")
    @Size(max = 100, message = "Hotel name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    private String description;

    @Min(value = 1, message = "Star rating must be at least 1")
    @Max(value = 5, message = "Star rating cannot exceed 5")
    private Integer starRating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<RoomDTO> rooms;
}
