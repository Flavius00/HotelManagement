package com.hotelchain.roommanagement.dto;

import lombok.*;
import com.hotelchain.roommanagement.entity.RoomType;
import com.hotelchain.roommanagement.entity.RoomPosition;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomFilterDTO {
    private String location;
    private Boolean isAvailable;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private RoomPosition position;
    private RoomType roomType;
    private List<String> facilities;
    private String sortBy; // "location", "roomNumber", "price"
    private String sortDirection; // "asc", "desc"
}
