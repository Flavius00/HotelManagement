package com.hotelchain.usermanagement.service;

import com.hotelchain.usermanagement.entity.NotificationType;
import java.util.List;

public interface NotificationService {
    void notifyUserInfoChange(Long userId, String message);
    void addNotificationObserver(NotificationObserver observer);
    void removeNotificationObserver(NotificationObserver observer);
    void sendNotification(Long userId, NotificationType type, String message);
    List<NotificationObserver> getObservers();
}
