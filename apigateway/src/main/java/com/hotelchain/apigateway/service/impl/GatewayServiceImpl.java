package com.hotelchain.apigateway.service.impl;

import com.hotelchain.apigateway.dto.ServiceResponse;
import com.hotelchain.apigateway.dto.AggregatedResponse;
import com.hotelchain.apigateway.entity.RequestLog;
import com.hotelchain.apigateway.repository.RequestLogRepository;
import com.hotelchain.apigateway.repository.SessionRepository;
import com.hotelchain.apigateway.service.GatewayService;
import com.hotelchain.apigateway.service.ServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayServiceImpl implements GatewayService {

    private final ServiceRegistry serviceRegistry;
    private final RequestLogRepository requestLogRepository;
    private final SessionRepository sessionRepository;
    private final RestTemplate restTemplate;

    @Override
    public ServiceResponse<Object> routeRequest(String serviceName, String endpoint, String method,
                                                Object body, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();

        try {
            String serviceUrl = serviceRegistry.getServiceUrl(serviceName);
            if (serviceUrl == null) {
                return ServiceResponse.builder()
                        .success(false)
                        .message("Service not found: " + serviceName)
                        .statusCode(404)
                        .serviceName(serviceName)
                        .responseTime(System.currentTimeMillis() - startTime)
                        .build();
            }

            String fullUrl = serviceUrl + endpoint;
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            if (headers != null) {
                headers.forEach(httpHeaders::set);
            }

            HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<Object> response;

            switch (method.toUpperCase()) {
                case "GET":
                    response = restTemplate.getForEntity(fullUrl, Object.class);
                    break;
                case "POST":
                    response = restTemplate.postForEntity(fullUrl, entity, Object.class);
                    break;
                case "PUT":
                    response = restTemplate.exchange(fullUrl, HttpMethod.PUT, entity, Object.class);
                    break;
                case "DELETE":
                    response = restTemplate.exchange(fullUrl, HttpMethod.DELETE, entity, Object.class);
                    break;
                case "PATCH":
                    response = restTemplate.exchange(fullUrl, HttpMethod.PATCH, entity, Object.class);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }

            return ServiceResponse.builder()
                    .data(response.getBody())
                    .success(response.getStatusCode().is2xxSuccessful())
                    .message("Request successful")
                    .statusCode(response.getStatusCodeValue())
                    .serviceName(serviceName)
                    .responseTime(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("Error routing request to service {}: {}", serviceName, e.getMessage());
            return ServiceResponse.builder()
                    .success(false)
                    .message("Service error: " + e.getMessage())
                    .statusCode(500)
                    .serviceName(serviceName)
                    .responseTime(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public AggregatedResponse aggregateData(String... serviceNames) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> aggregatedData = new ConcurrentHashMap<>();

        // Use CompletableFuture for parallel service calls
        CompletableFuture<Void>[] futures = new CompletableFuture[serviceNames.length];

        for (int i = 0; i < serviceNames.length; i++) {
            String serviceName = serviceNames[i];
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    ServiceResponse<Object> response = routeRequest(serviceName, "/api/health", "GET", null, null);
                    aggregatedData.put(serviceName, response);
                } catch (Exception e) {
                    log.error("Error aggregating data from service {}: {}", serviceName, e.getMessage());
                    aggregatedData.put(serviceName, Map.of("error", e.getMessage()));
                }
            });
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures).join();

        return AggregatedResponse.builder()
                .data(aggregatedData)
                .status("success")
                .message("Data aggregated successfully")
                .responseTime(System.currentTimeMillis() - startTime)
                .build();
    }

    @Override
    public void logRequest(String sessionId, String endpoint, String method, Integer statusCode,
                           Long responseTime, Long userId) {
        try {
            RequestLog requestLog = RequestLog.builder()
                    .sessionId(sessionId)
                    .endpoint(endpoint)
                    .method(method)
                    .statusCode(statusCode)
                    .responseTimeMs(responseTime)
                    .userId(userId)
                    .build();

            requestLogRepository.save(requestLog);
        } catch (Exception e) {
            log.error("Error logging request: {}", e.getMessage());
        }
    }

    @Override
    public boolean validateSession(String sessionId) {
        return sessionRepository.findByIdAndIsActive(sessionId, true).isPresent();
    }
}