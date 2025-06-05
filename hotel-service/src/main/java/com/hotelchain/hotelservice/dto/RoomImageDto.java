package com.hotelchain.hotelservice.dto;

import lombok.Data;

@Data
public class RoomImageDto {
    private Long id;
    private String imageUrl;
    private String description;
    private Integer displayOrder;
}