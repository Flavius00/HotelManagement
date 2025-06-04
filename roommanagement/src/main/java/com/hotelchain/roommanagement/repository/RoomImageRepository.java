package com.hotelchain.roommanagement.repository;

import com.hotelchain.roommanagement.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findByRoomIdOrderByImageOrder(Long roomId);
    List<RoomImage> findByRoomId(Long roomId);
}