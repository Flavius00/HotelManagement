package com.hotelchain.apigateway.dto;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregatedResponse {
    private Map<String, Object> data;
    private String status;
    private String message;
    private Long responseTime;
}