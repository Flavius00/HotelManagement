package com.hotelchain.hotelservice.repository;

import com.hotelchain.hotelservice.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findByRoomId(Long roomId);
    List<RoomImage> findByRoomIdOrderByDisplayOrder(Long roomId);
    void deleteByRoomId(Long roomId);
}