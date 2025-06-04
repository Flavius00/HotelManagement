package com.hotelchain.usermanagement.repository;

import com.hotelchain.usermanagement.entity.NotificationPreference;
import com.hotelchain.usermanagement.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    List<NotificationPreference> findByUserId(Long userId);
    List<NotificationPreference> findByUserIdAndIsEnabled(Long userId, Boolean isEnabled);
    List<NotificationPreference> findByUserIdAndNotificationType(Long userId, NotificationType notificationType);
}
