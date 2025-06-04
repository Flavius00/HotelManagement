package com.hotelchain.usermanagement.repository;

import com.hotelchain.usermanagement.entity.NotificationHistory;
import com.hotelchain.usermanagement.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    List<NotificationHistory> findByUserId(Long userId);
    List<NotificationHistory> findByStatus(NotificationStatus status);
    List<NotificationHistory> findByUserIdAndStatus(Long userId, NotificationStatus status);
}