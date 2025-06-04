package com.hotelchain.bookingreview.strategy.impl;

import com.hotelchain.bookingreview.dto.BookingDTO;
import com.hotelchain.bookingreview.strategy.ExportStrategy;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class XMLExportStrategy implements ExportStrategy {

    @Override
    public String export(List<BookingDTO> bookings, String filename) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<bookings>\n");

        for (BookingDTO booking : bookings) {
            xml.append("  <booking>\n")
                    .append("    <id>").append(booking.getId()).append("</id>\n")
                    .append("    <userId>").append(booking.getUserId()).append("</userId>\n")
                    .append("    <roomId>").append(booking.getRoomId()).append("</roomId>\n")
                    .append("    <checkInDate>").append(booking.getCheckInDate()).append("</checkInDate>\n")
                    .append("    <checkOutDate>").append(booking.getCheckOutDate()).append("</checkOutDate>\n")
                    .append("    <totalPrice>").append(booking.getTotalPrice()).append("</totalPrice>\n")
                    .append("    <status>").append(booking.getBookingStatus()).append("</status>\n")
                    .append("    <specialRequests>").append(escapeXml(booking.getSpecialRequests())).append("</specialRequests>\n")
                    .append("    <createdAt>").append(booking.getCreatedAt()).append("</createdAt>\n")
                    .append("    <createdBy>").append(booking.getCreatedBy()).append("</createdBy>\n")
                    .append("  </booking>\n");
        }

        xml.append("</bookings>");
        return xml.toString();
    }

    @Override
    public String getFileExtension() {
        return ".xml";
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
