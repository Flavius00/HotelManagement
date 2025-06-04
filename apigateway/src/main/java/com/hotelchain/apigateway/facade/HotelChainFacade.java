package com.hotelchain.apigateway.facade;

import com.hotelchain.apigateway.dto.ServiceResponse;
import com.hotelchain.apigateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelChainFacade {

    private final GatewayService gatewayService;

    // User Management Operations
    public ServiceResponse<Object> authenticateUser(Map<String, Object> loginData) {
        return gatewayService.routeRequest("user-management", "/api/users/login", "POST", loginData, null);
    }

    public ServiceResponse<Object> createUser(Map<String, Object> userData) {
        return gatewayService.routeRequest("user-management", "/api/users/register", "POST", userData, null);
    }

    public ServiceResponse<Object> getAllUsers() {
        return gatewayService.routeRequest("user-management", "/api/users", "GET", null, null);
    }

    // Room Management Operations
    public ServiceResponse<Object> getAllRooms() {
        return gatewayService.routeRequest("room-management", "/api/rooms", "GET", null, null);
    }

    public ServiceResponse<Object> getRoomsSorted() {
        return gatewayService.routeRequest("room-management", "/api/rooms/sorted", "GET", null, null);
    }

    public ServiceResponse<Object> getAvailableRooms() {
        return gatewayService.routeRequest("room-management", "/api/rooms/available", "GET", null, null);
    }

    public ServiceResponse<Object> filterRooms(Map<String, Object> filterCriteria) {
        return gatewayService.routeRequest("room-management", "/api/rooms/filter", "POST", filterCriteria, null);
    }

    public ServiceResponse<Object> createRoom(Map<String, Object> roomData) {
        return gatewayService.routeRequest("room-management", "/api/rooms", "POST", roomData, null);
    }

    // Booking and Review Operations
    public ServiceResponse<Object> createBooking(Map<String, Object> bookingData) {
        return gatewayService.routeRequest("booking-review", "/api/bookings", "POST", bookingData, null);
    }

    public ServiceResponse<Object> getAllBookings() {
        return gatewayService.routeRequest("booking-review", "/api/bookings", "GET", null, null);
    }

    public ServiceResponse<Object> getReviewsByRoom(Long roomId) {
        return gatewayService.routeRequest("booking-review", "/api/reviews/room/" + roomId, "GET", null, null);
    }

    public ServiceResponse<Object> createReview(Map<String, Object> reviewData) {
        return gatewayService.routeRequest("booking-review", "/api/reviews", "POST", reviewData, null);
    }

    public ServiceResponse<Object> exportBookings(Map<String, Object> exportParams) {
        return gatewayService.routeRequest("booking-review", "/api/bookings/export", "POST", exportParams, null);
    }

    // Aggregated Operations
    public Map<String, Object> getDashboardData() {
        log.info("Aggregating dashboard data from all services");

        Map<String, Object> dashboardData = new HashMap<>();

        try {
            ServiceResponse<Object> users = getAllUsers();
            ServiceResponse<Object> rooms = getAllRooms();
            ServiceResponse<Object> bookings = getAllBookings();

            dashboardData.put("users", users.getData());
            dashboardData.put("rooms", rooms.getData());
            dashboardData.put("bookings", bookings.getData());
            dashboardData.put("status", "success");

        } catch (Exception e) {
            log.error("Error aggregating dashboard data: {}", e.getMessage());
            dashboardData.put("status", "error");
            dashboardData.put("message", e.getMessage());
        }

        return dashboardData;
    }
}