package com.hotelchain.roommanagement.service.impl;

import com.hotelchain.roommanagement.dto.*;
import com.hotelchain.roommanagement.entity.Room;
import com.hotelchain.roommanagement.repository.RoomRepository;
import com.hotelchain.roommanagement.service.RoomService;
import com.hotelchain.roommanagement.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public RoomDTO createRoom(RoomDTO roomDTO) {
        log.info("Creating new room: {} for hotel ID: {}", roomDTO.getRoomNumber(), roomDTO.getHotelId());

        Room room = roomMapper.toEntity(roomDTO);
        Room savedRoom = roomRepository.save(room);

        log.info("Room created successfully with ID: {}", savedRoom.getId());
        return roomMapper.toDTO(savedRoom);
    }

    @Override
    public RoomDTO updateRoom(Long id, RoomDTO roomDTO) {
        log.info("Updating room with ID: {}", id);

        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        existingRoom.setRoomNumber(roomDTO.getRoomNumber());
        existingRoom.setRoomType(roomDTO.getRoomType());
        existingRoom.setPrice(roomDTO.getPrice());
        existingRoom.setPosition(roomDTO.getPosition());
        existingRoom.setMaxOccupancy(roomDTO.getMaxOccupancy());
        existingRoom.setDescription(roomDTO.getDescription());
        existingRoom.setIsAvailable(roomDTO.getIsAvailable());

        Room updatedRoom = roomRepository.save(existingRoom);

        log.info("Room updated successfully with ID: {}", updatedRoom.getId());
        return roomMapper.toDTO(updatedRoom);
    }

    @Override
    public void deleteRoom(Long id) {
        log.info("Deleting room with ID: {}", id);

        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found");
        }

        roomRepository.deleteById(id);
        log.info("Room deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomDTO> getRoomById(Long id) {
        return roomRepository.findById(id)
                .map(roomMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId).stream()
                .map(roomMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getAvailableRooms() {
        return roomRepository.findByIsAvailable(true).stream()
                .map(roomMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> filterRooms(RoomFilterDTO filterDTO) {
        log.info("Filtering rooms with criteria: {}", filterDTO);

        List<Room> rooms = roomRepository.findRoomsWithFilters(
                filterDTO.getLocation(),
                filterDTO.getIsAvailable(),
                filterDTO.getMinPrice(),
                filterDTO.getMaxPrice(),
                filterDTO.getPosition(),
                filterDTO.getRoomType()
        );

        // Additional filtering by facilities if specified
        if (filterDTO.getFacilities() != null && !filterDTO.getFacilities().isEmpty()) {
            List<Room> roomsWithFacilities = roomRepository.findByFacilities(filterDTO.getFacilities());
            rooms.retainAll(roomsWithFacilities);
        }

        return rooms.stream()
                .map(roomMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsSortedByLocationAndNumber() {
        return roomRepository.findByLocationSorted("").stream()
                .map(roomMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateRoomAvailability(Long roomId, Boolean isAvailable) {
        log.info("Updating room availability for ID: {} to: {}", roomId, isAvailable);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setIsAvailable(isAvailable);
        roomRepository.save(room);

        log.info("Room availability updated successfully for ID: {}", roomId);
    }
}
