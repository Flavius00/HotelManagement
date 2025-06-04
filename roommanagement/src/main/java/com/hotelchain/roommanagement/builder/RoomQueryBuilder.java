package com.hotelchain.roommanagement.builder;

import com.hotelchain.roommanagement.dto.RoomFilterDTO;
import com.hotelchain.roommanagement.entity.RoomType;
import com.hotelchain.roommanagement.entity.RoomPosition;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
public class RoomQueryBuilder {
    private RoomFilterDTO filter = new RoomFilterDTO();

    public RoomQueryBuilder location(String location) {
        filter.setLocation(location);
        return this;
    }

    public RoomQueryBuilder availability(Boolean isAvailable) {
        filter.setIsAvailable(isAvailable);
        return this;
    }

    public RoomQueryBuilder priceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        return this;
    }

    public RoomQueryBuilder position(RoomPosition position) {
        filter.setPosition(position);
        return this;
    }

    public RoomQueryBuilder roomType(RoomType roomType) {
        filter.setRoomType(roomType);
        return this;
    }

    public RoomQueryBuilder facilities(List<String> facilities) {
        filter.setFacilities(facilities);
        return this;
    }

    public RoomQueryBuilder sortBy(String sortBy, String direction) {
        filter.setSortBy(sortBy);
        filter.setSortDirection(direction);
        return this;
    }

    public RoomFilterDTO build() {
        return filter;
    }
}