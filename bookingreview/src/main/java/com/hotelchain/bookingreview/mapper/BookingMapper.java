package com.hotelchain.bookingreview.mapper;

import com.hotelchain.bookingreview.dto.BookingDTO;
import com.hotelchain.bookingreview.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDTO toDTO(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Booking toEntity(BookingDTO bookingDTO);
}