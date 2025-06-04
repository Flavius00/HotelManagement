package com.hotelchain.bookingreview.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistics", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"metric_name", "metric_date", "hotel_id", "room_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "metric_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal metricValue;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}