package com.hotelchain.apigateway.service;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class ServiceRegistry {
    private final Map<String, String> serviceUrls = new ConcurrentHashMap<>();

    public ServiceRegistry() {
        // Initialize service URLs
        serviceUrls.put("user-management", "http://localhost:8081");
        serviceUrls.put("room-management", "http://localhost:8082");
        serviceUrls.put("booking-review", "http://localhost:8083");
    }

    public String getServiceUrl(String serviceName) {
        return serviceUrls.get(serviceName);
    }

    public void registerService(String serviceName, String url) {
        serviceUrls.put(serviceName, url);
    }

    public void unregisterService(String serviceName) {
        serviceUrls.remove(serviceName);
    }

    public boolean isServiceRegistered(String serviceName) {
        return serviceUrls.containsKey(serviceName);
    }
}