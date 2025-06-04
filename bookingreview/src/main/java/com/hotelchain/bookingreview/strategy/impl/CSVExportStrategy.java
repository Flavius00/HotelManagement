package com.hotelchain.bookingreview.strategy.impl;

import com.hotelchain.bookingreview.dto.BookingDTO;
import com.hotelchain.bookingreview.strategy.ExportStrategy;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CSVExportStrategy implements ExportStrategy {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public String export(List<BookingDTO> bookings, String filename) {
        StringBuilder csv = new StringBuilder();

        // Header
        csv.append("ID,User ID,Room ID,Check In,Check Out,Total Price,Status,Special Requests,Created At,Created By\n");

        // Data rows
        for (BookingDTO booking : bookings) {
            csv.append(escapeCsv(String.valueOf(booking.getId()))).append(",")
                    .append(escapeCsv(String.valueOf(booking.getUserId()))).append(",")
                    .append(escapeCsv(String.valueOf(booking.getRoomId()))).append(",")
                    .append(escapeCsv(booking.getCheckInDate().format(DATE_FORMATTER))).append(",")
                    .append(escapeCsv(booking.getCheckOutDate().format(DATE_FORMATTER))).append(",")
                    .append(escapeCsv(String.valueOf(booking.getTotalPrice()))).append(",")
                    .append(escapeCsv(String.valueOf(booking.getBookingStatus()))).append(",")
                    .append(escapeCsv(booking.getSpecialRequests() != null ? booking.getSpecialRequests() : "")).append(",")
                    .append(escapeCsv(booking.getCreatedAt() != null ? booking.getCreatedAt().format(DATETIME_FORMATTER) : "")).append(",")
                    .append(escapeCsv(booking.getCreatedBy() != null ? String.valueOf(booking.getCreatedBy()) : "")).append("\n");
        }

        return csv.toString();
    }

    @Override
    public String getFileExtension() {
        return ".csv";
    }

    private String escapeCsv(String value) {
        if (value == null) return "";

        // Înlocuiește ghilimelele duble cu două ghilimele duble
        value = value.replace("\"", "\"\"");

        // Dacă valoarea conține virgule, ghilimele sau newlines, o încadrează în ghilimele
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            value = "\"" + value + "\"";
        }

        return value;
    }
}
