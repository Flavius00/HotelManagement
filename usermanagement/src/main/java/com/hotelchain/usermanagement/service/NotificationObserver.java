package com.hotelchain.usermanagement.service;

import com.hotelchain.usermanagement.entity.NotificationType;

public interface NotificationObserver {
    void onNotificationSent(Long userId, NotificationType type, String message);
    NotificationType getSupportedType();
}