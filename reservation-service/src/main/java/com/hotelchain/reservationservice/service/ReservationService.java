package com.hotelchain.reservationservice.service;

import com.hotelchain.reservationservice.dto.*;
import com.hotelchain.reservationservice.entity.Reservation;
import com.hotelchain.reservationservice.entity.ReservationStatus;
import com.hotelchain.reservationservice.repository.ReservationRepository;
import com.hotelchain.reservationservice.adapter.ExternalServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JwtValidationService jwtValidationService;

    @Autowired
    private ExternalServiceAdapter externalServiceAdapter;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.services.user:http://localhost:8081}")
    private String userServiceUrl;

    @Value("${app.services.hotel:http://localhost:8082}")
    private String hotelServiceUrl;

    @Value("${app.services.export:true}")
    private boolean exportEnabled;

    // EMPLOYEE METHODS - pentru angajați

    /**
     * Creează o rezervare nouă (doar EMPLOYEE, MANAGER, ADMIN)
     */
    @Transactional
    public ReservationDto createReservation(CreateReservationRequest request, String token) {
        jwtValidationService.validateEmployeeRole(token);

        Long employeeId = jwtValidationService.getUserIdFromToken(token);
        Long clientId = request.getClientId();

        // Dacă clientul nu există, creează unul nou
        if (clientId == null) {
            clientId = createNewClient(request, token);
        }

        // Verifică disponibilitatea camerei
        if (!isRoomAvailable(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate())) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        // Creează rezervarea
        Reservation reservation = new Reservation();
        reservation.setRoomId(request.getRoomId());
        reservation.setClientId(clientId);
        reservation.setEmployeeId(employeeId);
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setTotalPrice(request.getTotalPrice());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        reservation = reservationRepository.save(reservation);

        // Trimite email de confirmare către client
        sendConfirmationEmail(reservation);

        return convertToDto(reservation);
    }

    /**
     * Obține toate rezervările
     */
    public List<ReservationDto> getAllReservations(String token) {
        jwtValidationService.validateEmployeeRole(token);

        String role = jwtValidationService.getRoleFromToken(token);
        Long employeeHotelId = jwtValidationService.getHotelIdFromToken(token);

        List<Reservation> reservations;

        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            // Admin și Manager pot vedea toate rezervările
            reservations = reservationRepository.findAll();
        } else {
            // Employee poate vedea doar rezervările pentru hotelul său
            reservations = reservationRepository.findByEmployeeHotelId(employeeHotelId);
        }

        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obține rezervările pentru un client specific
     */
    public List<ReservationDto> getReservationsByClient(Long clientId, String token) {
        jwtValidationService.validateEmployeeRole(token);

        List<Reservation> reservations = reservationRepository.findByClientIdOrderByCreatedAtDesc(clientId);
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Actualizează statusul unei rezervări
     */
    public ReservationDto updateReservationStatus(Long reservationId, String status, String token) {
        jwtValidationService.validateEmployeeRole(token);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        try {
            ReservationStatus newStatus = ReservationStatus.valueOf(status.toUpperCase());
            reservation.setStatus(newStatus);
            reservation = reservationRepository.save(reservation);

            // Notifică clientul despre schimbarea statusului
            notifyClientStatusChange(reservation, newStatus);

            return convertToDto(reservation);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    /**
     * Anulează o rezervare
     */
    public ReservationDto cancelReservation(Long reservationId, String token) {
        jwtValidationService.validateEmployeeRole(token);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation = reservationRepository.save(reservation);

        // Notifică clientul despre anulare
        notifyCancellation(reservation);

        return convertToDto(reservation);
    }

    /**
     * Export rezervări în diferite formate
     */
    public byte[] exportReservations(String format, String token) {
        jwtValidationService.validateEmployeeRole(token);

        List<ReservationDto> reservations = getAllReservations(token);

        return switch (format.toLowerCase()) {
            case "csv" -> exportToCsv(reservations);
            case "json" -> exportToJson(reservations);
            case "xml" -> exportToXml(reservations);
            case "doc" -> exportToDoc(reservations);
            default -> throw new RuntimeException("Unsupported format: " + format);
        };
    }

    // HELPER METHODS

    private Long createNewClient(CreateReservationRequest request, String token) {
        try {
            Map<String, Object> clientData = new HashMap<>();
            clientData.put("username", request.getClientUsername());
            clientData.put("password", request.getClientPassword());
            clientData.put("email", request.getClientEmail());
            clientData.put("phone", request.getClientPhone());
            clientData.put("role", "CLIENT");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(clientData, headers);

            String url = userServiceUrl + "/api/users/create";
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Long.valueOf(response.getBody().get("id").toString());
            } else {
                throw new RuntimeException("Failed to create client account");
            }
        } catch (Exception e) {
            // Dacă crearea automată eșuează, trimite email către admin
            sendNewClientRequestEmail(request);
            throw new RuntimeException("Failed to create client account automatically. Admin has been notified via email.");
        }
    }

    private boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        List<Reservation> conflictingReservations = reservationRepository
                .findConflictingReservations(roomId, checkIn, checkOut);
        return conflictingReservations.isEmpty();
    }

    private void sendConfirmationEmail(Reservation reservation) {
        try {
            String clientEmail = getClientEmail(reservation.getClientId());
            String roomInfo = getRoomInfo(reservation.getRoomId());

            String subject = "Reservation Confirmation - HotelChain";
            String message = String.format(
                    "Your reservation has been confirmed!\n\n" +
                            "Reservation ID: %d\n" +
                            "Room: %s\n" +
                            "Check-in: %s\n" +
                            "Check-out: %s\n" +
                            "Total Price: $%.2f\n\n" +
                            "Thank you for choosing HotelChain!",
                    reservation.getId(),
                    roomInfo,
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate(),
                    reservation.getTotalPrice()
            );

            externalServiceAdapter.sendEmail(clientEmail, subject, message);
        } catch (Exception e) {
            // Log error but don't fail the reservation
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
    }

    private void sendNewClientRequestEmail(CreateReservationRequest request) {
        try {
            String adminEmail = "admin@hotelchain.com";
            String subject = "New Client Account Request";
            String message = String.format(
                    "A new client account creation was requested:\n\n" +
                            "Username: %s\n" +
                            "Email: %s\n" +
                            "Phone: %s\n\n" +
                            "Please create this account manually in the admin panel.",
                    request.getClientUsername(),
                    request.getClientEmail(),
                    request.getClientPhone()
            );

            externalServiceAdapter.sendEmail(adminEmail, subject, message);
        } catch (Exception e) {
            System.err.println("Failed to send new client request email: " + e.getMessage());
        }
    }

    private void notifyClientStatusChange(Reservation reservation, ReservationStatus newStatus) {
        try {
            String clientEmail = getClientEmail(reservation.getClientId());
            String subject = "Reservation Status Update - HotelChain";
            String message = String.format(
                    "Your reservation status has been updated:\n\n" +
                            "Reservation ID: %d\n" +
                            "New Status: %s\n" +
                            "Check-in: %s\n" +
                            "Check-out: %s",
                    reservation.getId(),
                    newStatus.name(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate()
            );

            externalServiceAdapter.sendEmail(clientEmail, subject, message);
        } catch (Exception e) {
            System.err.println("Failed to send status change notification: " + e.getMessage());
        }
    }

    private void notifyCancellation(Reservation reservation) {
        try {
            String clientEmail = getClientEmail(reservation.getClientId());
            String subject = "Reservation Cancelled - HotelChain";
            String message = String.format(
                    "Your reservation has been cancelled:\n\n" +
                            "Reservation ID: %d\n" +
                            "Check-in Date: %s\n" +
                            "Check-out Date: %s\n\n" +
                            "If you have any questions, please contact us.",
                    reservation.getId(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate()
            );

            externalServiceAdapter.sendEmail(clientEmail, subject, message);
        } catch (Exception e) {
            System.err.println("Failed to send cancellation notification: " + e.getMessage());
        }
    }

    private String getClientEmail(Long clientId) {
        try {
            String url = userServiceUrl + "/api/users/" + clientId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("email");
            }
        } catch (Exception e) {
            System.err.println("Failed to get client email: " + e.getMessage());
        }
        return "unknown@hotelchain.com";
    }

    private String getRoomInfo(Long roomId) {
        try {
            String url = hotelServiceUrl + "/api/hotels/rooms/" + roomId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> room = response.getBody();
                return String.format("Room %s at %s",
                        room.get("roomNumber"),
                        room.get("hotelName"));
            }
        } catch (Exception e) {
            System.err.println("Failed to get room info: " + e.getMessage());
        }
        return "Room #" + roomId;
    }

    private byte[] exportToCsv(List<ReservationDto> reservations) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Room,Hotel,Client,Employee,Check-in,Check-out,Total Price,Status\n");

        for (ReservationDto reservation : reservations) {
            csv.append(String.format("%d,%s,%s,%s,%s,%s,%s,%.2f,%s\n",
                    reservation.getId(),
                    reservation.getRoomNumber(),
                    reservation.getHotelName(),
                    reservation.getClientName(),
                    reservation.getEmployeeName(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate(),
                    reservation.getTotalPrice(),
                    reservation.getStatus()
            ));
        }

        return csv.toString().getBytes();
    }

    private byte[] exportToJson(List<ReservationDto> reservations) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsBytes(reservations);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export to JSON", e);
        }
    }

    private byte[] exportToXml(List<ReservationDto> reservations) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<reservations>\n");

        for (ReservationDto reservation : reservations) {
            xml.append("  <reservation>\n");
            xml.append("    <id>").append(reservation.getId()).append("</id>\n");
            xml.append("    <roomNumber>").append(reservation.getRoomNumber()).append("</roomNumber>\n");
            xml.append("    <hotelName>").append(reservation.getHotelName()).append("</hotelName>\n");
            xml.append("    <clientName>").append(reservation.getClientName()).append("</clientName>\n");
            xml.append("    <checkInDate>").append(reservation.getCheckInDate()).append("</checkInDate>\n");
            xml.append("    <checkOutDate>").append(reservation.getCheckOutDate()).append("</checkOutDate>\n");
            xml.append("    <totalPrice>").append(reservation.getTotalPrice()).append("</totalPrice>\n");
            xml.append("    <status>").append(reservation.getStatus()).append("</status>\n");
            xml.append("  </reservation>\n");
        }

        xml.append("</reservations>");
        return xml.toString().getBytes();
    }

    private byte[] exportToDoc(List<ReservationDto> reservations) {
        // Implementation using Apache POI for DOC export
        // This is a simplified version - you'd need proper POI implementation
        StringBuilder doc = new StringBuilder();
        doc.append("RESERVATIONS REPORT\n\n");

        for (ReservationDto reservation : reservations) {
            doc.append(String.format(
                    "Reservation ID: %d\n" +
                            "Room: %s at %s\n" +
                            "Client: %s\n" +
                            "Dates: %s to %s\n" +
                            "Total: $%.2f\n" +
                            "Status: %s\n\n",
                    reservation.getId(),
                    reservation.getRoomNumber(),
                    reservation.getHotelName(),
                    reservation.getClientName(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate(),
                    reservation.getTotalPrice(),
                    reservation.getStatus()
            ));
        }

        return doc.toString().getBytes();
    }

    private ReservationDto convertToDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setRoomId(reservation.getRoomId());
        dto.setClientId(reservation.getClientId());
        dto.setEmployeeId(reservation.getEmployeeId());
        dto.setCheckInDate(reservation.getCheckInDate());
        dto.setCheckOutDate(reservation.getCheckOutDate());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setStatus(reservation.getStatus().name());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());

        // Încarcă informații suplimentare prin API-uri externe
        try {
            dto.setRoomNumber(getRoomInfo(reservation.getRoomId()));
            dto.setClientEmail(getClientEmail(reservation.getClientId()));
        } catch (Exception e) {
            // Log but don't fail
            System.err.println("Failed to load additional reservation info: " + e.getMessage());
        }

        return dto;
    }
}