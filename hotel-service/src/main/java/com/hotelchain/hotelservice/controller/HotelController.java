package com.hotelchain.hotelservice.controller;

import com.hotelchain.hotelservice.dto.*;
import com.hotelchain.hotelservice.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "*")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    // Test endpoints
    @GetMapping("/test")
    public String test() {
        return "Hotel Service is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"UP\",\"service\":\"hotel-service\"}";
    }

    // PUBLIC ENDPOINTS - fără autentificare

    /**
     * Obține toate hotelurile active
     */
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllActiveHotels() {
        return ResponseEntity.ok(hotelService.getAllActiveHotels());
    }

    /**
     * Obține un hotel după ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(hotelService.getHotelById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Caută hoteluri după locație
     */
    @GetMapping("/search/location")
    public ResponseEntity<List<HotelDto>> searchHotelsByLocation(@RequestParam String location) {
        return ResponseEntity.ok(hotelService.searchHotelsByLocation(location));
    }

    /**
     * Caută hoteluri cu criterii multiple
     */
    @GetMapping("/search")
    public ResponseEntity<List<HotelDto>> searchHotels(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(required = false) Integer maxStars) {
        return ResponseEntity.ok(hotelService.searchHotels(location, name, minStars, maxStars));
    }

    // MANAGER ENDPOINTS - cu autentificare

    /**
     * Creează un hotel nou
     */
    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody CreateHotelRequest request,
                                                @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(hotelService.createHotel(request, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Actualizează un hotel
     */
    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long id,
                                                @RequestBody UpdateHotelRequest request,
                                                @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(hotelService.updateHotel(id, request, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Șterge un hotel
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id,
                                            @RequestHeader("Authorization") String token) {
        try {
            hotelService.deleteHotel(id, token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obține toate hotelurile (pentru manageri)
     */
    @GetMapping("/all")
    public ResponseEntity<List<HotelDto>> getAllHotels(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(hotelService.getAllHotels(token));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
        }
    }
}