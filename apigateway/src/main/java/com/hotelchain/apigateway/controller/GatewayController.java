package com.hotelchain.apigateway.controller;

import com.hotelchain.apigateway.dto.AggregatedResponse;
import com.hotelchain.apigateway.dto.ServiceResponse;
import com.hotelchain.apigateway.facade.HotelChainFacade;
import com.hotelchain.apigateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GatewayController {

    private final HotelChainFacade hotelChainFacade;
    private final GatewayService gatewayService;

    // Authentication endpoints
    @PostMapping("/auth/login")
    public ResponseEntity<ServiceResponse<Object>> login(@RequestBody Map<String, Object> loginData) {
        ServiceResponse<Object> response = hotelChainFacade.authenticateUser(loginData);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ServiceResponse<Object>> register(@RequestBody Map<String, Object> userData) {
        ServiceResponse<Object> response = hotelChainFacade.createUser(userData);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // User management endpoints
    @GetMapping("/users")
    public ResponseEntity<ServiceResponse<Object>> getAllUsers() {
        ServiceResponse<Object> response = hotelChainFacade.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Room management endpoints
    @GetMapping("/rooms")
    public ResponseEntity<ServiceResponse<Object>> getAllRooms() {
        ServiceResponse<Object> response = hotelChainFacade.getAllRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/rooms/sorted")
    public ResponseEntity<ServiceResponse<Object>> getRoomsSorted() {
        ServiceResponse<Object> response = hotelChainFacade.getRoomsSorted();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/rooms/available")
    public ResponseEntity<ServiceResponse<Object>> getAvailableRooms() {
        ServiceResponse<Object> response = hotelChainFacade.getAvailableRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/rooms/filter")
    public ResponseEntity<ServiceResponse<Object>> filterRooms(@RequestBody Map<String, Object> filterCriteria) {
        ServiceResponse<Object> response = hotelChainFacade.filterRooms(filterCriteria);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/rooms")
    public ResponseEntity<ServiceResponse<Object>> createRoom(@RequestBody Map<String, Object> roomData) {
        ServiceResponse<Object> response = hotelChainFacade.createRoom(roomData);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Booking endpoints
    @PostMapping("/bookings")
    public ResponseEntity<ServiceResponse<Object>> createBooking(@RequestBody Map<String, Object> bookingData) {
        ServiceResponse<Object> response = hotelChainFacade.createBooking(bookingData);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/bookings")
    public ResponseEntity<ServiceResponse<Object>> getAllBookings() {
        ServiceResponse<Object> response = hotelChainFacade.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/bookings/export")
    public ResponseEntity<ServiceResponse<Object>> exportBookings(@RequestBody Map<String, Object> exportParams) {
        ServiceResponse<Object> response = hotelChainFacade.exportBookings(exportParams);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Review endpoints
    @GetMapping("/reviews/room/{roomId}")
    public ResponseEntity<ServiceResponse<Object>> getReviewsByRoom(@PathVariable Long roomId) {
        ServiceResponse<Object> response = hotelChainFacade.getReviewsByRoom(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/reviews")
    public ResponseEntity<ServiceResponse<Object>> createReview(@RequestBody Map<String, Object> reviewData) {
        ServiceResponse<Object> response = hotelChainFacade.createReview(reviewData);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Aggregated endpoints
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboardData = hotelChainFacade.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/health")
    public ResponseEntity<AggregatedResponse> getHealthStatus() {
        AggregatedResponse response = gatewayService.aggregateData(
                "user-management", "room-management", "booking-review");
        return ResponseEntity.ok(response);
    }

    // Generic routing endpoint
    @RequestMapping("/{serviceName}/**")
    public ResponseEntity<ServiceResponse<Object>> routeToService(
            @PathVariable String serviceName,
            @RequestBody(required = false) Object body,
            @RequestHeader Map<String, String> headers,
            jakarta.servlet.http.HttpServletRequest request) {

        String endpoint = request.getRequestURI().substring(("/api/gateway/" + serviceName).length());
        String method = request.getMethod();

        ServiceResponse<Object> response = gatewayService.routeRequest(serviceName, endpoint, method, body, headers);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}