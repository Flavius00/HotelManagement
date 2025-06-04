package com.hotelchain.bookingreview.mapper;

import com.hotelchain.bookingreview.dto.ReviewDTO;
import com.hotelchain.bookingreview.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "bookingId", source = "booking.id")
    ReviewDTO toDTO(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking.id", source = "bookingId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toEntity(ReviewDTO reviewDTO);
}