package com.hotelchain.usermanagement.service.impl;

import com.hotelchain.usermanagement.entity.NotificationHistory;
import com.hotelchain.usermanagement.entity.NotificationPreference;
import com.hotelchain.usermanagement.entity.NotificationType;
import com.hotelchain.usermanagement.entity.NotificationStatus;
import com.hotelchain.usermanagement.repository.NotificationHistoryRepository;
import com.hotelchain.usermanagement.repository.NotificationPreferenceRepository;
import com.hotelchain.usermanagement.service.NotificationObserver;
import com.hotelchain.usermanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final List<NotificationObserver> observers = new ArrayList<>();

    @Override
    public void notifyUserInfoChange(Long userId, String message) {
        log.info("Sending notification to user {} with message: {}", userId, message);

        // Găsește preferințele de notificare ale utilizatorului
        List<NotificationPreference> preferences = notificationPreferenceRepository
                .findByUserIdAndIsEnabled(userId, true);

        if (preferences.isEmpty()) {
            log.info("No notification preferences found for user {}", userId);
            return;
        }

        // Trimite notificarea pentru fiecare tip preferat
        for (NotificationPreference preference : preferences) {
            sendNotification(userId, preference.getNotificationType(), message);
        }
    }

    @Override
    public void sendNotification(Long userId, NotificationType type, String message) {
        try {
            log.info("Sending {} notification to user {}: {}", type, userId, message);

            // Salvează în istoric
            NotificationHistory history = NotificationHistory.builder()
                    .userId(userId)
                    .notificationType(type)
                    .message(message)
                    .status(NotificationStatus.PENDING)
                    .build();

            notificationHistoryRepository.save(history);

            // Notifică observatorii (Observer Pattern)
            notifyObservers(userId, type, message);

            // Actualizează statusul ca SENT
            history.setStatus(NotificationStatus.SENT);
            notificationHistoryRepository.save(history);

            log.info("Notification sent successfully to user {}", userId);

        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", userId, e.getMessage());

            // Salvează ca FAILED în istoric
            NotificationHistory failedHistory = NotificationHistory.builder()
                    .userId(userId)
                    .notificationType(type)
                    .message(message)
                    .status(NotificationStatus.FAILED)
                    .build();

            notificationHistoryRepository.save(failedHistory);
        }
    }

    @Override
    public void addNotificationObserver(NotificationObserver observer) {
        observers.add(observer);
        log.info("Added notification observer for type: {}", observer.getSupportedType());
    }

    @Override
    public void removeNotificationObserver(NotificationObserver observer) {
        observers.remove(observer);
        log.info("Removed notification observer for type: {}", observer.getSupportedType());
    }

    @Override
    public List<NotificationObserver> getObservers() {
        return new ArrayList<>(observers);
    }

    private void notifyObservers(Long userId, NotificationType type, String message) {
        for (NotificationObserver observer : observers) {
            if (observer.getSupportedType() == type) {
                try {
                    observer.onNotificationSent(userId, type, message);
                } catch (Exception e) {
                    log.error("Error notifying observer for type {}: {}", type, e.getMessage());
                }
            }
        }
    }
}