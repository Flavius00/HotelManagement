package com.hotelchain.apigateway.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestLogDTO {
    private Long id;
    private String sessionId;
    private String endpoint;
    private String method;
    private Integer statusCode;
    private Long responseTimeMs;
    private Long userId;
    private LocalDateTime timestamp;
}
