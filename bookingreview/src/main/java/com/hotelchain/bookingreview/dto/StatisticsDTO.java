package com.hotelchain.bookingreview.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {
    private Long id;
    private String metricName;
    private BigDecimal metricValue;
    private LocalDate metricDate;
    private Long hotelId;
    private Long roomId;
}