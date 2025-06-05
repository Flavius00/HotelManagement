package com.hotelchain.hotelservice.controller;

import com.hotelchain.hotelservice.dto.*;
import com.hotelchain.hotelservice.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // PUBLIC ENDPOINTS - fără autentificare

    /**
     * Obține camerele unui hotel, sortate după locație și număr
     */
    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<RoomDto>> getRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotelSorted(hotelId));
    }

    /**
     * Caută camere cu filtrare
     */
    @GetMapping("/rooms/search")
    public ResponseEntity<List<RoomDto>> searchRooms(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) List<String> facilities,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Integer minGuests,
            @RequestParam(required = false, defaultValue = "location") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {

        RoomSearchCriteria criteria = new RoomSearchCriteria();
        criteria.setLocation(location);
        criteria.setAvailable(available);
        criteria.setMinPrice(minPrice);
        criteria.setMaxPrice(maxPrice);
        criteria.setPosition(position);
        criteria.setFacilities(facilities);
        criteria.setRoomType(roomType);
        criteria.setMinGuests(minGuests);
        criteria.setSortBy(sortBy);
        criteria.setSortDirection(sortDirection);

        return ResponseEntity.ok(roomService.searchRooms(criteria));
    }

    /**
     * Obține toate camerele disponibile
     */
    @GetMapping("/rooms/available")
    public ResponseEntity<List<RoomDto>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    /**
     * Obține o cameră după ID
     */
    @GetMapping("/rooms/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(roomService.getRoomById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // EMPLOYEE ENDPOINTS - cu autentificare

    /**
     * Creează o cameră nouă
     */
    @PostMapping("/rooms")
    public ResponseEntity<RoomDto> createRoom(@RequestBody CreateRoomRequest request,
                                              @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(roomService.createRoom(request, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Actualizează o cameră
     */
    @PutMapping("/rooms/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id,
                                              @RequestBody CreateRoomRequest request,
                                              @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(roomService.updateRoom(id, request, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Șterge o cameră
     */
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id,
                                           @RequestHeader("Authorization") String token) {
        try {
            roomService.deleteRoom(id, token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Schimbă disponibilitatea unei camere
     */
    @PostMapping("/rooms/{id}/toggle-availability")
    public ResponseEntity<RoomDto> toggleRoomAvailability(@PathVariable Long id,
                                                          @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(roomService.toggleRoomAvailability(id, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}