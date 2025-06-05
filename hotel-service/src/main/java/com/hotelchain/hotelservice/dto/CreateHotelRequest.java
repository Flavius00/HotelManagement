package com.hotelchain.hotelservice.dto;

import lombok.Data;

@Data
public class CreateHotelRequest {
    private String name;
    private String location;
    private String address;
    private String description;
    private Integer starRating; // 1-5 stars
}