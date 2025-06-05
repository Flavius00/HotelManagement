package com.hotelchain.apigateway.controller;

import com.hotelchain.apigateway.factory.ResponseFactory;
import com.hotelchain.apigateway.factory.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*")
public class GatewayController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.services.user}")
    private String userServiceUrl;

    @Value("${app.services.hotel}")
    private String hotelServiceUrl;

    @Value("${app.services.reservation}")
    private String reservationServiceUrl;

    @Value("${app.services.review}")
    private String reviewServiceUrl;

    // Health check pentru gateway
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        ResponseFactory factory = ResponseFactory.getFactory(ResponseType.SUCCESS);
        return factory.createResponse("API Gateway is running",
                "{\"service\":\"api-gateway\",\"version\":\"1.0\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}");
    }

    // ==========================================
    // AUTH ROUTES (login, register, validate)
    // ==========================================

    @PostMapping("/api/auth/login")
    public ResponseEntity<String> login(@RequestBody String body) {
        return forwardToUserService("/api/users/login", "POST", body, null);
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<String> register(@RequestBody String body) {
        return forwardToUserService("/api/users/register", "POST", body, null);
    }

    @PostMapping("/api/auth/validate-token")
    public ResponseEntity<String> validateToken(@RequestBody String body) {
        return forwardToUserService("/api/users/validate-token", "POST", body, null);
    }

    // ==========================================
    // USER ROUTES
    // ==========================================

    @GetMapping("/api/users/**")
    public ResponseEntity<String> getUsersGet(HttpServletRequest request,
                                              @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/users");
        String queryString = request.getQueryString();
        if (queryString != null) {
            path += "?" + queryString;
        }
        return forwardToUserService("/api/users" + path, "GET", null, token);
    }

    @PostMapping("/api/users/**")
    public ResponseEntity<String> getUsersPost(HttpServletRequest request,
                                               @RequestBody String body,
                                               @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/users");
        return forwardToUserService("/api/users" + path, "POST", body, token);
    }

    @PutMapping("/api/users/**")
    public ResponseEntity<String> getUsersPut(HttpServletRequest request,
                                              @RequestBody String body,
                                              @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/users");
        return forwardToUserService("/api/users" + path, "PUT", body, token);
    }

    @DeleteMapping("/api/users/**")
    public ResponseEntity<String> getUsersDelete(HttpServletRequest request,
                                                 @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/users");
        return forwardToUserService("/api/users" + path, "DELETE", null, token);
    }

    // ==========================================
    // HOTEL ROUTES
    // ==========================================

    @GetMapping("/api/hotels/**")
    public ResponseEntity<String> getHotels(HttpServletRequest request,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/hotels");
        String queryString = request.getQueryString();
        if (queryString != null) {
            path += "?" + queryString;
        }
        return forwardToHotelService("/api/hotels" + path, "GET", null, token);
    }

    @PostMapping("/api/hotels/**")
    public ResponseEntity<String> postHotels(HttpServletRequest request,
                                             @RequestBody String body,
                                             @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/hotels");
        return forwardToHotelService("/api/hotels" + path, "POST", body, token);
    }

    @PutMapping("/api/hotels/**")
    public ResponseEntity<String> putHotels(HttpServletRequest request,
                                            @RequestBody String body,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/hotels");
        return forwardToHotelService("/api/hotels" + path, "PUT", body, token);
    }

    @DeleteMapping("/api/hotels/**")
    public ResponseEntity<String> deleteHotels(HttpServletRequest request,
                                               @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/hotels");
        return forwardToHotelService("/api/hotels" + path, "DELETE", null, token);
    }

    // ==========================================
    // RESERVATION ROUTES
    // ==========================================

    @GetMapping("/api/reservations/**")
    public ResponseEntity<String> getReservations(HttpServletRequest request,
                                                  @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/reservations");
        String queryString = request.getQueryString();
        if (queryString != null) {
            path += "?" + queryString;
        }
        return forwardToReservationService("/api/reservations" + path, "GET", null, token);
    }

    @PostMapping("/api/reservations/**")
    public ResponseEntity<String> postReservations(HttpServletRequest request,
                                                   @RequestBody String body,
                                                   @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/reservations");
        return forwardToReservationService("/api/reservations" + path, "POST", body, token);
    }

    @PutMapping("/api/reservations/**")
    public ResponseEntity<String> putReservations(HttpServletRequest request,
                                                  @RequestBody String body,
                                                  @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/reservations");
        return forwardToReservationService("/api/reservations" + path, "PUT", body, token);
    }

    @DeleteMapping("/api/reservations/**")
    public ResponseEntity<String> deleteReservations(HttpServletRequest request,
                                                     @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/reservations");
        return forwardToReservationService("/api/reservations" + path, "DELETE", null, token);
    }

    // ==========================================
    // REVIEW ROUTES
    // ==========================================

    @GetMapping("/api/reviews/**")
    public ResponseEntity<String> getReviews(HttpServletRequest request,
                                             @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/reviews");
        String queryString = request.getQueryString();
        if (queryString != null) {
            path += "?" + queryString;
        }
        return forwardToReviewService("/api/reviews" + path, "GET", null, token);
    }

    @PostMapping("/api/reviews/**")
    public ResponseEntity<String> postReviews(HttpServletRequest request,
                                              @RequestBody String body,
                                              @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/reviews");
        return forwardToReviewService("/api/reviews" + path, "POST", body, token);
    }

    @PutMapping("/api/reviews/**")
    public ResponseEntity<String> putReviews(HttpServletRequest request,
                                             @RequestBody String body,
                                             @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/reviews");
        return forwardToReviewService("/api/reviews" + path, "PUT", body, token);
    }

    @DeleteMapping("/api/reviews/**")
    public ResponseEntity<String> deleteReviews(HttpServletRequest request,
                                                @RequestHeader(value = "Authorization", required = false) String token) {
        String path = extractPath(request, "/api/reviews");
        return forwardToReviewService("/api/reviews" + path, "DELETE", null, token);
    }

    // ==========================================
    // TEST ROUTES
    // ==========================================

    @GetMapping("/test/user")
    public ResponseEntity<String> testUserService() {
        try {
            String url = userServiceUrl + "/api/users/test";
            String response = restTemplate.getForObject(url, String.class);
            ResponseFactory factory = ResponseFactory.getFactory(ResponseType.SUCCESS);
            return factory.createResponse("User service test completed",
                    "{\"gateway\":\"OK\",\"user-service\":\"" + response + "\"}");
        } catch (Exception e) {
            ResponseFactory factory = ResponseFactory.getFactory(ResponseType.ERROR);
            return factory.createResponse("User service unavailable", "SERVICE_DOWN");
        }
    }

    @GetMapping("/test/hotel")
    public ResponseEntity<String> testHotelService() {
        try {
            String url = hotelServiceUrl + "/api/hotels/test";
            String response = restTemplate.getForObject(url, String.class);
            ResponseFactory factory = ResponseFactory.getFactory(ResponseType.SUCCESS);
            return factory.createResponse("Hotel service test completed",
                    "{\"gateway\":\"OK\",\"hotel-service\":\"" + response + "\"}");
        } catch (Exception e) {
            ResponseFactory factory = ResponseFactory.getFactory(ResponseType.ERROR);
            return factory.createResponse("Hotel service unavailable", "SERVICE_DOWN");
        }
    }

    @GetMapping("/config")
    public ResponseEntity<String> showConfig() {
        ResponseFactory factory = ResponseFactory.getFactory(ResponseType.SUCCESS);
        String configData = String.format(
                "{\"userServiceUrl\":\"%s\",\"hotelServiceUrl\":\"%s\",\"reservationServiceUrl\":\"%s\",\"reviewServiceUrl\":\"%s\"}",
                userServiceUrl, hotelServiceUrl, reservationServiceUrl, reviewServiceUrl);
        return factory.createResponse("Configuration retrieved", configData);
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private ResponseEntity<String> forwardToUserService(String path, String method, String body, String token) {
        return forwardRequest(userServiceUrl + path, method, body, token);
    }

    private ResponseEntity<String> forwardToHotelService(String path, String method, String body, String token) {
        return forwardRequest(hotelServiceUrl + path, method, body, token);
    }

    private ResponseEntity<String> forwardToReservationService(String path, String method, String body, String token) {
        return forwardRequest(reservationServiceUrl + path, method, body, token);
    }

    private ResponseEntity<String> forwardToReviewService(String path, String method, String body, String token) {
        return forwardRequest(reviewServiceUrl + path, method, body, token);
    }

    private ResponseEntity<String> forwardRequest(String url, String method, String body, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", token);
            }

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            HttpMethod httpMethod = switch (method.toUpperCase()) {
                case "GET" -> HttpMethod.GET;
                case "POST" -> HttpMethod.POST;
                case "PUT" -> HttpMethod.PUT;
                case "DELETE" -> HttpMethod.DELETE;
                default -> HttpMethod.GET;
            };

            ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, entity, String.class);

            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            ResponseFactory factory = ResponseFactory.getFactory(ResponseType.ERROR);
            return factory.createResponse("Service unavailable: " + e.getMessage(), "GATEWAY_ERROR");
        }
    }

    private String extractPath(HttpServletRequest request, String prefix) {
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith(prefix)) {
            String path = requestURI.substring(prefix.length());
            return path.isEmpty() ? "" : path;
        }
        return "";
    }
}