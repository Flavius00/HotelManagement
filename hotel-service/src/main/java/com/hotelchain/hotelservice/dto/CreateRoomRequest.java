package com.hotelchain.hotelservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateRoomRequest {
    private Long hotelId;
    private String roomNumber;
    private String roomType; // SINGLE, DOUBLE, SUITE, DELUXE, FAMILY
    private BigDecimal pricePerNight;
    private Integer floorNumber;
    private String position; // OCEAN_VIEW, GARDEN_VIEW, CITY_VIEW, MOUNTAIN_VIEW
    private Integer sizeSqm;
    private Integer maxGuests;
    private List<String> facilities; // WiFi, TV, Air Conditioning, etc.
    private List<String> imageUrls; // URLs pentru imagini
}