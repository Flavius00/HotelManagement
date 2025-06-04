package com.hotelchain.roommanagement.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomFacilityDTO {
    private Long id;

    @NotBlank(message = "Facility name is required")
    @Size(max = 100, message = "Facility name cannot exceed 100 characters")
    private String facilityName;

    private String facilityDescription;
}