package com.hotelchain.apigateway.service;

import com.hotelchain.apigateway.dto.ServiceResponse;
import com.hotelchain.apigateway.dto.AggregatedResponse;
import java.util.Map;

public interface GatewayService {
    ServiceResponse<Object> routeRequest(String serviceName, String endpoint, String method, Object body, Map<String, String> headers);
    AggregatedResponse aggregateData(String... serviceNames);
    void logRequest(String sessionId, String endpoint, String method, Integer statusCode, Long responseTime, Long userId);
    boolean validateSession(String sessionId);
}