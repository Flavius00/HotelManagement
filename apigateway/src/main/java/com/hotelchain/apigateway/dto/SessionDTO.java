package com.hotelchain.apigateway.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDTO {
    private String id;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    private String userAgent;
    private String ipAddress;
}
