package com.hotelchain.hotelservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomSearchCriteria {
    private String location; // Locația hotelului
    private Boolean available; // Disponibilitate
    private BigDecimal minPrice; // Preț minim pe noapte
    private BigDecimal maxPrice; // Preț maxim pe noapte
    private String position; // Poziția camerei (view)
    private List<String> facilities; // Facilitățile dorite
    private String roomType; // Tipul camerei
    private Integer minGuests; // Numărul minim de oaspeți
    private String sortBy; // Sortare după: location, roomNumber, price
    private String sortDirection; // ASC sau DESC
}