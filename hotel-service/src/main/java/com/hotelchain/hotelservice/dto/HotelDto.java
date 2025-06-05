package com.hotelchain.hotelservice.dto;

import lombok.Data;

@Data
public class HotelDto {
    private Long id;
    private String name;
    private String location;
    private String address;
    private String description;
    private Integer starRating;
    private boolean active;
}