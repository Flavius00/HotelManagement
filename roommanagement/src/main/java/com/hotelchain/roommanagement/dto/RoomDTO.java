package com.hotelchain.roommanagement.dto;

import lombok.*;
import com.hotelchain.roommanagement.entity.RoomType;
import com.hotelchain.roommanagement.entity.RoomPosition;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Long id;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotBlank(message = "Room number is required")
    @Size(max = 10, message = "Room number cannot exceed 10 characters")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Position is required")
    private RoomPosition position;

    private Boolean isAvailable;

    @Min(value = 1, message = "Max occupancy must be at least 1")
    private Integer maxOccupancy;

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<RoomImageDTO> images;
    private Set<RoomFacilityDTO> facilities;
}