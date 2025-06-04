package com.hotelchain.roommanagement.service;

import com.hotelchain.roommanagement.dto.*;
import com.hotelchain.roommanagement.entity.RoomType;
import com.hotelchain.roommanagement.entity.RoomPosition;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    RoomDTO createRoom(RoomDTO roomDTO);
    RoomDTO updateRoom(Long id, RoomDTO roomDTO);
    void deleteRoom(Long id);
    Optional<RoomDTO> getRoomById(Long id);
    List<RoomDTO> getAllRooms();
    List<RoomDTO> getRoomsByHotel(Long hotelId);
    List<RoomDTO> getAvailableRooms();
    List<RoomDTO> filterRooms(RoomFilterDTO filterDTO);
    List<RoomDTO> getRoomsSortedByLocationAndNumber();
    void updateRoomAvailability(Long roomId, Boolean isAvailable);
}