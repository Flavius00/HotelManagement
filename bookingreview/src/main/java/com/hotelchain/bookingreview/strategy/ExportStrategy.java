package com.hotelchain.bookingreview.strategy;

import com.hotelchain.bookingreview.dto.BookingDTO;
import java.util.List;

public interface ExportStrategy {
    String export(List<BookingDTO> bookings, String filename);
    String getFileExtension();
}