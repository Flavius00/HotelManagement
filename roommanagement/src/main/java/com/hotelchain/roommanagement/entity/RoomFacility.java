package com.hotelchain.roommanagement.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "room_facilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "room")
@ToString(exclude = "room")
public class RoomFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "facility_name", nullable = false, length = 100)
    private String facilityName;

    @Column(name = "facility_description", columnDefinition = "TEXT")
    private String facilityDescription;
}