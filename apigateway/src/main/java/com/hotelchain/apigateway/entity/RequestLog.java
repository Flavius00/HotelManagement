package com.hotelchain.apigateway.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private String sessionId;

    @Column(nullable = false, length = 500)
    private String endpoint;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "user_id")
    private Long userId;

    @Column
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;
}