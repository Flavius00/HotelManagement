package com.hotelchain.bookingreview.service;

import com.hotelchain.bookingreview.dto.BookingDTO;
import com.hotelchain.bookingreview.entity.FileFormat;
import com.hotelchain.bookingreview.strategy.ExportStrategy;
import com.hotelchain.bookingreview.strategy.impl.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final CSVExportStrategy csvExportStrategy;
    private final JSONExportStrategy jsonExportStrategy;
    private final XMLExportStrategy xmlExportStrategy;
    private final DOCExportStrategy docExportStrategy;

    @Value("${export.file-path:/tmp/hotel-exports/}")
    private String exportPath;

    public String exportBookings(List<BookingDTO> bookings, FileFormat format, String filename) {
        ExportStrategy strategy = getStrategy(format);
        String content = strategy.export(bookings, filename);

        // Opțional: salvează fișierul pe disk
        String filePath = saveToFile(content, filename, strategy.getFileExtension());

        log.info("Exported {} bookings to {} format. File saved at: {}",
                bookings.size(), format, filePath);

        return content;
    }

    public String exportBookingsToFile(List<BookingDTO> bookings, FileFormat format, String filename) {
        ExportStrategy strategy = getStrategy(format);
        String content = strategy.export(bookings, filename);
        String filePath = saveToFile(content, filename, strategy.getFileExtension());

        log.info("Exported {} bookings to file: {}", bookings.size(), filePath);
        return filePath;
    }

    private ExportStrategy getStrategy(FileFormat format) {
        switch (format) {
            case CSV:
                return csvExportStrategy;
            case JSON:
                return jsonExportStrategy;
            case XML:
                return xmlExportStrategy;
            case DOC:
                return docExportStrategy;
            default:
                throw new IllegalArgumentException("Unsupported export format: " + format);
        }
    }

    private String saveToFile(String content, String filename, String extension) {
        try {
            // Creează directorul dacă nu există
            File directory = new File(exportPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generează numele fișierului cu timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fullFilename = filename + "_" + timestamp + extension;
            String fullPath = exportPath + fullFilename;

            // Scrie fișierul
            try (FileWriter writer = new FileWriter(fullPath)) {
                writer.write(content);
            }

            return fullPath;

        } catch (IOException e) {
            log.error("Error saving export file: {}", e.getMessage());
            throw new RuntimeException("Failed to save export file", e);
        }
    }
}