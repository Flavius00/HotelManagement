package com.hotelchain.roommanagement.mapper;

import com.hotelchain.roommanagement.dto.RoomDTO;
import com.hotelchain.roommanagement.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "hotelId", source = "hotel.id")
    RoomDTO toDTO(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel.id", source = "hotelId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "availabilities", ignore = true)
    Room toEntity(RoomDTO roomDTO);
}