package com.hotelchain.hotelservice.service;

import com.hotelchain.hotelservice.dto.*;
import com.hotelchain.hotelservice.entity.*;
import com.hotelchain.hotelservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomFacilityRepository facilityRepository;

    @Autowired
    private RoomImageRepository imageRepository;

    @Autowired
    private JwtValidationService jwtValidationService;

    // PUBLIC METHODS - fără autentificare

    /**
     * Obține toate camerele pentru un hotel, sortate după locație și număr
     */
    public List<RoomDto> getRoomsByHotelSorted(Long hotelId) {
        List<Room> rooms = roomRepository.findByHotelIdAndActiveTrue(hotelId);
        return rooms.stream()
                .sorted((r1, r2) -> {
                    // Sortare după hotel.location apoi după roomNumber
                    int locationCompare = r1.getHotel().getLocation().compareTo(r2.getHotel().getLocation());
                    if (locationCompare != 0) return locationCompare;
                    return r1.getRoomNumber().compareTo(r2.getRoomNumber());
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Caută camere cu criterii de filtrare
     */
    public List<RoomDto> searchRooms(RoomSearchCriteria criteria) {
        RoomPosition position = null;
        if (criteria.getPosition() != null) {
            try {
                position = RoomPosition.valueOf(criteria.getPosition().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Poziție invalidă, ignoră
            }
        }

        RoomType roomType = null;
        if (criteria.getRoomType() != null) {
            try {
                roomType = RoomType.valueOf(criteria.getRoomType().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Tip invalid, ignoră
            }
        }

        List<Room> rooms = roomRepository.findRoomsWithCriteria(
                criteria.getLocation(),
                criteria.getAvailable(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                position,
                roomType,
                criteria.getMinGuests()
        );

        // Filtrare suplimentară pe facilități dacă sunt specificate
        if (criteria.getFacilities() != null && !criteria.getFacilities().isEmpty()) {
            rooms = rooms.stream()
                    .filter(room -> {
                        List<String> roomFacilities = room.getFacilities().stream()
                                .map(RoomFacility::getFacility)
                                .collect(Collectors.toList());
                        return roomFacilities.containsAll(criteria.getFacilities());
                    })
                    .collect(Collectors.toList());
        }

        // Sortare
        if (criteria.getSortBy() != null) {
            rooms = sortRooms(rooms, criteria.getSortBy(), criteria.getSortDirection());
        }

        return rooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obține toate camerele disponibile
     */
    public List<RoomDto> getAvailableRooms() {
        List<Room> rooms = roomRepository.findByAvailableTrueAndActiveTrue();
        return rooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obține o cameră după ID
     */
    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return convertToDto(room);
    }

    // EMPLOYEE METHODS - cu autentificare

    /**
     * Creează o cameră nouă (doar EMPLOYEE, MANAGER, ADMIN)
     */
    @Transactional
    public RoomDto createRoom(CreateRoomRequest request, String token) {
        jwtValidationService.validateEmployeeRole(token);

        // Verifică dacă hotelul există
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        // Verifică dacă camera există deja în acest hotel
        List<Room> existingRooms = roomRepository.findByHotelIdAndActiveTrue(request.getHotelId());
        boolean roomExists = existingRooms.stream()
                .anyMatch(r -> r.getRoomNumber().equals(request.getRoomNumber()));

        if (roomExists) {
            throw new RuntimeException("Room with this number already exists in this hotel");
        }

        Room room = new Room();
        room.setHotelId(request.getHotelId());
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(RoomType.valueOf(request.getRoomType().toUpperCase()));
        room.setPricePerNight(request.getPricePerNight());
        room.setFloorNumber(request.getFloorNumber());
        room.setPosition(RoomPosition.valueOf(request.getPosition().toUpperCase()));
        room.setSizeSqm(request.getSizeSqm());
        room.setMaxGuests(request.getMaxGuests());
        room.setAvailable(true);
        room.setActive(true);

        room = roomRepository.save(room);

        // Adaugă facilitățile
        if (request.getFacilities() != null) {
            for (String facility : request.getFacilities()) {
                RoomFacility roomFacility = new RoomFacility();
                roomFacility.setRoomId(room.getId());
                roomFacility.setFacility(facility);
                facilityRepository.save(roomFacility);
            }
        }

        // Adaugă imaginile
        if (request.getImageUrls() != null) {
            int order = 1;
            for (String imageUrl : request.getImageUrls()) {
                RoomImage roomImage = new RoomImage();
                roomImage.setRoomId(room.getId());
                roomImage.setImageUrl(imageUrl);
                roomImage.setDisplayOrder(order++);
                imageRepository.save(roomImage);
            }
        }

        return convertToDto(room);
    }

    /**
     * Actualizează o cameră
     */
    @Transactional
    public RoomDto updateRoom(Long id, CreateRoomRequest request, String token) {
        jwtValidationService.validateEmployeeRole(token);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Actualizează datele de bază
        if (request.getRoomNumber() != null) {
            room.setRoomNumber(request.getRoomNumber());
        }
        if (request.getRoomType() != null) {
            room.setRoomType(RoomType.valueOf(request.getRoomType().toUpperCase()));
        }
        if (request.getPricePerNight() != null) {
            room.setPricePerNight(request.getPricePerNight());
        }
        if (request.getFloorNumber() != null) {
            room.setFloorNumber(request.getFloorNumber());
        }
        if (request.getPosition() != null) {
            room.setPosition(RoomPosition.valueOf(request.getPosition().toUpperCase()));
        }
        if (request.getSizeSqm() != null) {
            room.setSizeSqm(request.getSizeSqm());
        }
        if (request.getMaxGuests() != null) {
            room.setMaxGuests(request.getMaxGuests());
        }

        room = roomRepository.save(room);

        // Actualizează facilitățile
        if (request.getFacilities() != null) {
            // Șterge facilitățile existente
            facilityRepository.deleteAll(facilityRepository.findByRoomId(room.getId()));

            // Adaugă facilitățile noi
            for (String facility : request.getFacilities()) {
                RoomFacility roomFacility = new RoomFacility();
                roomFacility.setRoomId(room.getId());
                roomFacility.setFacility(facility);
                facilityRepository.save(roomFacility);
            }
        }

        return convertToDto(room);
    }

    /**
     * Șterge o cameră (soft delete)
     */
    public void deleteRoom(Long id, String token) {
        jwtValidationService.validateEmployeeRole(token);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setActive(false);
        roomRepository.save(room);
    }

    /**
     * Schimbă disponibilitatea unei camere
     */
    public RoomDto toggleRoomAvailability(Long id, String token) {
        jwtValidationService.validateEmployeeRole(token);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setAvailable(!room.isAvailable());
        room = roomRepository.save(room);

        return convertToDto(room);
    }

    // HELPER METHODS

    private List<Room> sortRooms(List<Room> rooms, String sortBy, String sortDirection) {
        boolean ascending = !"DESC".equalsIgnoreCase(sortDirection);

        return rooms.stream()
                .sorted((r1, r2) -> {
                    int result = 0;
                    switch (sortBy.toLowerCase()) {
                        case "location":
                            result = r1.getHotel().getLocation().compareTo(r2.getHotel().getLocation());
                            break;
                        case "roomnumber":
                            result = r1.getRoomNumber().compareTo(r2.getRoomNumber());
                            break;
                        case "price":
                            result = r1.getPricePerNight().compareTo(r2.getPricePerNight());
                            break;
                        default:
                            result = r1.getId().compareTo(r2.getId());
                    }
                    return ascending ? result : -result;
                })
                .collect(Collectors.toList());
    }

    private RoomDto convertToDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setHotelId(room.getHotelId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomType(room.getRoomType().name());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setFloorNumber(room.getFloorNumber());
        dto.setPosition(room.getPosition().name());
        dto.setSizeSqm(room.getSizeSqm());
        dto.setMaxGuests(room.getMaxGuests());
        dto.setAvailable(room.isAvailable());
        dto.setActive(room.isActive());

        // Adaugă informații despre hotel
        if (room.getHotel() != null) {
            dto.setHotelName(room.getHotel().getName());
            dto.setHotelLocation(room.getHotel().getLocation());
        } else {
            // Încarcă hotelul dacă nu este deja încărcat
            Hotel hotel = hotelRepository.findById(room.getHotelId()).orElse(null);
            if (hotel != null) {
                dto.setHotelName(hotel.getName());
                dto.setHotelLocation(hotel.getLocation());
            }
        }

        // Adaugă facilitățile
        List<RoomFacility> facilities = facilityRepository.findByRoomId(room.getId());
        dto.setFacilities(facilities.stream()
                .map(RoomFacility::getFacility)
                .collect(Collectors.toList()));

        // Adaugă imaginile
        List<RoomImage> images = imageRepository.findByRoomIdOrderByDisplayOrder(room.getId());
        dto.setImages(images.stream()
                .map(this::convertImageToDto)
                .collect(Collectors.toList()));

        return dto;
    }

    private RoomImageDto convertImageToDto(RoomImage image) {
        RoomImageDto dto = new RoomImageDto();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setDescription(image.getDescription());
        dto.setDisplayOrder(image.getDisplayOrder());
        return dto;
    }
}