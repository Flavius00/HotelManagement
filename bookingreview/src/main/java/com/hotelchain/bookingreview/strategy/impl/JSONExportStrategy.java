package com.hotelchain.bookingreview.strategy.impl;

import com.hotelchain.bookingreview.dto.BookingDTO;
import com.hotelchain.bookingreview.strategy.ExportStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JSONExportStrategy implements ExportStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public String export(List<BookingDTO> bookings, String filename) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(bookings);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting to JSON", e);
        }
    }

    @Override
    public String getFileExtension() {
        return ".json";
    }
}