package com.hotelchain.roommanagement.repository;

import com.hotelchain.roommanagement.entity.RoomFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomFacilityRepository extends JpaRepository<RoomFacility, Long> {
    List<RoomFacility> findByRoomId(Long roomId);
    List<RoomFacility> findByFacilityNameContainingIgnoreCase(String facilityName);
}