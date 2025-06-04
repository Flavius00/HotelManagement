package com.hotelchain.roommanagement.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomImageDTO {
    private Long id;

    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @Min(value = 1, message = "Image order must be at least 1")
    @Max(value = 3, message = "Image order cannot exceed 3")
    private Integer imageOrder;

    private String altText;
}