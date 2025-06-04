package com.hotelchain.apigateway.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse<T> {
    private T data;
    private boolean success;
    private String message;
    private int statusCode;
    private String serviceName;
    private long responseTime;
}