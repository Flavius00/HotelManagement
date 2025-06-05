package com.hotelchain.reviewservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDto {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String hotelName;
    private Long clientId;
    private String clientName;
    private Long reservationId;
    private Integer rating;
    private String title;
    private String comment;
    private LocalDateTime createdAt;
    private boolean active;
}