package com.hotelchain.hotelservice.service;

import com.hotelchain.hotelservice.dto.*;
import com.hotelchain.hotelservice.entity.Hotel;
import com.hotelchain.hotelservice.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private JwtValidationService jwtValidationService;

    // PUBLIC METHODS - fără autentificare

    /**
     * Obține toate hotelurile active
     */
    public List<HotelDto> getAllActiveHotels() {
        List<Hotel> hotels = hotelRepository.findByActiveTrue();
        return hotels.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obține un hotel după ID
     */
    public HotelDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        return convertToDto(hotel);
    }

    /**
     * Caută hoteluri după locație
     */
    public List<HotelDto> searchHotelsByLocation(String location) {
        List<Hotel> hotels = hotelRepository.findByLocationContainingIgnoreCaseAndActiveTrue(location);
        return hotels.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Caută hoteluri cu criterii multiple
     */
    public List<HotelDto> searchHotels(String location, String name, Integer minStars, Integer maxStars) {
        List<Hotel> hotels = hotelRepository.findHotelsWithCriteria(location, name, minStars, maxStars);
        return hotels.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // MANAGER METHODS - cu autentificare

    /**
     * Creează un hotel nou (doar MANAGER, ADMIN)
     */
    public HotelDto createHotel(CreateHotelRequest request, String token) {
        jwtValidationService.validateManagerRole(token);

        Hotel hotel = new Hotel();
        hotel.setName(request.getName());
        hotel.setLocation(request.getLocation());
        hotel.setAddress(request.getAddress());
        hotel.setDescription(request.getDescription());
        hotel.setStarRating(request.getStarRating() != null ? request.getStarRating() : 3);
        hotel.setActive(true);

        hotel = hotelRepository.save(hotel);
        return convertToDto(hotel);
    }

    /**
     * Actualizează un hotel
     */
    public HotelDto updateHotel(Long id, UpdateHotelRequest request, String token) {
        jwtValidationService.validateManagerRole(token);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        if (request.getName() != null) {
            hotel.setName(request.getName());
        }
        if (request.getLocation() != null) {
            hotel.setLocation(request.getLocation());
        }
        if (request.getAddress() != null) {
            hotel.setAddress(request.getAddress());
        }
        if (request.getDescription() != null) {
            hotel.setDescription(request.getDescription());
        }
        if (request.getStarRating() != null) {
            hotel.setStarRating(request.getStarRating());
        }
        if (request.getActive() != null) {
            hotel.setActive(request.getActive());
        }

        hotel = hotelRepository.save(hotel);
        return convertToDto(hotel);
    }

    /**
     * Șterge un hotel (soft delete)
     */
    public void deleteHotel(Long id, String token) {
        jwtValidationService.validateManagerRole(token);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        hotel.setActive(false);
        hotelRepository.save(hotel);
    }

    /**
     * Obține toate hotelurile (inclusiv inactive) pentru manageri
     */
    public List<HotelDto> getAllHotels(String token) {
        jwtValidationService.validateManagerRole(token);

        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // HELPER METHODS

    private HotelDto convertToDto(Hotel hotel) {
        HotelDto dto = new HotelDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setLocation(hotel.getLocation());
        dto.setAddress(hotel.getAddress());
        dto.setDescription(hotel.getDescription());
        dto.setStarRating(hotel.getStarRating());
        dto.setActive(hotel.isActive());
        return dto;
    }
}