package com.hotelchain.roommanagement.controller;

import com.hotelchain.roommanagement.dto.*;
import com.hotelchain.roommanagement.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomDTO roomDTO) {
        try {
            RoomDTO createdRoom = roomService.createRoom(roomDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
        } catch (RuntimeException e) {
            log.error("Error creating room: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<RoomDTO>> getRoomsSorted() {
        List<RoomDTO> rooms = roomService.getRoomsSortedByLocationAndNumber();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms() {
        List<RoomDTO> rooms = roomService.getAvailableRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
                .map(room -> ResponseEntity.ok(room))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomDTO>> getRoomsByHotel(@PathVariable Long hotelId) {
        List<RoomDTO> rooms = roomService.getRoomsByHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<RoomDTO>> filterRooms(@RequestBody RoomFilterDTO filterDTO) {
        List<RoomDTO> rooms = roomService.filterRooms(filterDTO);
        return ResponseEntity.ok(rooms);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Long id,
                                              @Valid @RequestBody RoomDTO roomDTO) {
        try {
            RoomDTO updatedRoom = roomService.updateRoom(id, roomDTO);
            return ResponseEntity.ok(updatedRoom);
        } catch (RuntimeException e) {
            log.error("Error updating room: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> updateRoomAvailability(@PathVariable Long id,
                                                       @RequestParam Boolean isAvailable) {
        try {
            roomService.updateRoomAvailability(id, isAvailable);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error updating room availability: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting room: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
