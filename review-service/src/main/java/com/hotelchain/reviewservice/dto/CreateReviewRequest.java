package com.hotelchain.reviewservice.dto;

import lombok.Data;

@Data
public class CreateReviewRequest {
    private Long roomId;
    private Long reservationId;
    private Integer rating; // 1-5 stars
    private String title;
    private String comment;
}