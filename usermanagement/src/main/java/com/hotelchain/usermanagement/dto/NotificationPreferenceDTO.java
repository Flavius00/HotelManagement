package com.hotelchain.usermanagement.dto;

import lombok.*;
import com.hotelchain.usermanagement.entity.NotificationType;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceDTO {
    private Long id;

    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;

    private Boolean isEnabled;
    private String contactInfo;
}