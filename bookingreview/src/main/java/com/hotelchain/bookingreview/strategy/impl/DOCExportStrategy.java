package com.hotelchain.bookingreview.strategy.impl;

import com.hotelchain.bookingreview.dto.BookingDTO;
import com.hotelchain.bookingreview.strategy.ExportStrategy;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DOCExportStrategy implements ExportStrategy {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public String export(List<BookingDTO> bookings, String filename) {
        StringBuilder doc = new StringBuilder();
        doc.append("HOTEL CHAIN - BOOKING REPORT\n");
        doc.append("========================================\n\n");
        doc.append("Generated on: ").append(java.time.LocalDateTime.now().format(DATETIME_FORMATTER)).append("\n");
        doc.append("Total bookings: ").append(bookings.size()).append("\n\n");

        for (int i = 0; i < bookings.size(); i++) {
            BookingDTO booking = bookings.get(i);
            doc.append("BOOKING #").append(i + 1).append("\n");
            doc.append("----------------------------------------\n");
            doc.append("Booking ID: ").append(booking.getId()).append("\n");
            doc.append("User ID: ").append(booking.getUserId()).append("\n");
            doc.append("Room ID: ").append(booking.getRoomId()).append("\n");
            doc.append("Check-in Date: ").append(booking.getCheckInDate().format(DATE_FORMATTER)).append("\n");
            doc.append("Check-out Date: ").append(booking.getCheckOutDate().format(DATE_FORMATTER)).append("\n");
            doc.append("Total Price: $").append(booking.getTotalPrice()).append("\n");
            doc.append("Status: ").append(booking.getBookingStatus()).append("\n");

            if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().isEmpty()) {
                doc.append("Special Requests: ").append(booking.getSpecialRequests()).append("\n");
            }

            if (booking.getCreatedAt() != null) {
                doc.append("Created: ").append(booking.getCreatedAt().format(DATETIME_FORMATTER)).append("\n");
            }

            if (booking.getCreatedBy() != null) {
                doc.append("Created by: ").append(booking.getCreatedBy()).append("\n");
            }

            doc.append("\n");
        }

        doc.append("========================================\n");
        doc.append("End of Report\n");
        doc.append("Hotel Chain Management System\n");

        return doc.toString();
    }

    @Override
    public String getFileExtension() {
        return ".doc";
    }
}