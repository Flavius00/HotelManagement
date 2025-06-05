package com.hotelchain.hotelservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomDto {
    private Long id;
    private Long hotelId;
    private String hotelName;
    private String hotelLocation;
    private String roomNumber;
    private String roomType;
    private BigDecimal pricePerNight;
    private Integer floorNumber;
    private String position;
    private Integer sizeSqm;
    private Integer maxGuests;
    private boolean available;
    private boolean active;
    private List<String> facilities;
    private List<RoomImageDto> images;
}