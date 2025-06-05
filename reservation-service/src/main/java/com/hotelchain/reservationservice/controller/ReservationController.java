package com.hotelchain.reservationservice.controller;

import com.hotelchain.reservationservice.dto.*;
import com.hotelchain.reservationservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // Test endpoints
    @GetMapping("/test")
    public String test() {
        return "Reservation Service is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"UP\",\"service\":\"reservation-service\"}";
    }

    // EMPLOYEE ENDPOINTS - cu autentificare

    /**
     * Creează o rezervare nouă
     */
    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody CreateReservationRequest request,
                                                            @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reservationService.createReservation(request, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Obține toate rezervările
     */
    @GetMapping
    public ResponseEntity<List<ReservationDto>> getAllReservations(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reservationService.getAllReservations(token));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * Obține rezervările pentru un client specific
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ReservationDto>> getReservationsByClient(@PathVariable Long clientId,
                                                                        @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reservationService.getReservationsByClient(clientId, token));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * Actualizează statusul unei rezervări
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationDto> updateReservationStatus(@PathVariable Long id,
                                                                  @RequestParam String status,
                                                                  @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reservationService.updateReservationStatus(id, status, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Anulează o rezervare
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationDto> cancelReservation(@PathVariable Long id,
                                                            @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(reservationService.cancelReservation(id, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Export rezervări în diferite formate
     */
    @GetMapping("/export/{format}")
    public ResponseEntity<byte[]> exportReservations(@PathVariable String format,
                                                     @RequestHeader("Authorization") String token) {
        try {
            byte[] data = reservationService.exportReservations(format, token);

            HttpHeaders headers = new HttpHeaders();
            String filename = "reservations." + format.toLowerCase();

            switch (format.toLowerCase()) {
                case "csv":
                    headers.setContentType(MediaType.parseMediaType("text/csv"));
                    break;
                case "json":
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    break;
                case "xml":
                    headers.setContentType(MediaType.APPLICATION_XML);
                    break;
                case "doc":
                    headers.setContentType(MediaType.parseMediaType("application/msword"));
                    break;
                default:
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }

            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(data.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}