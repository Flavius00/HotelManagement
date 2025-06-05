package com.hotelchain.reservationservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservationDto {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String hotelName;
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private Long employeeId;
    private String employeeName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}