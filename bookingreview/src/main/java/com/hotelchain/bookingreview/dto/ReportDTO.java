package com.hotelchain.bookingreview.dto;

import lombok.*;
import com.hotelchain.bookingreview.entity.ReportType;
import com.hotelchain.bookingreview.entity.FileFormat;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    private Long id;
    private String reportName;
    private ReportType reportType;
    private String filePath;
    private FileFormat fileFormat;
    private Long generatedBy;
    private LocalDateTime createdAt;
    private String parameters;
}