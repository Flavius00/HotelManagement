package com.hotelchain.hotelservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Column(name = "price_per_night", precision = 10, scale = 2, nullable = false)
    private BigDecimal pricePerNight;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Enumerated(EnumType.STRING)
    private RoomPosition position = RoomPosition.CITY_VIEW;

    @Column(name = "size_sqm")
    private Integer sizeSqm;

    @Column(name = "max_guests")
    private Integer maxGuests = 2;

    private boolean available = true;
    private boolean active = true;

    // Relația cu hotelul
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;

    // Relația cu facilitățile
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomFacility> facilities;

    // Relația cu imaginile
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomImage> images;
}