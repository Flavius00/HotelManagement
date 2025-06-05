package com.hotelchain.hotelservice.repository;

import com.hotelchain.hotelservice.entity.RoomFacility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomFacilityRepository extends JpaRepository<RoomFacility, Long> {
    List<RoomFacility> findByRoomId(Long roomId);
    void deleteByRoomId(Long roomId);
}