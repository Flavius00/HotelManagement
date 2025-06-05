package com.hotelchain.reservationservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateReservationRequest {
    private Long roomId;
    private Long clientId; // Dacă clientul există deja

    // Pentru client nou (dacă clientId este null)
    private String clientUsername;
    private String clientPassword;
    private String clientEmail;
    private String clientPhone;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
}