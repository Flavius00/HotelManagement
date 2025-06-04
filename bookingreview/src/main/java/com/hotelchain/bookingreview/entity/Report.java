package com.hotelchain.bookingreview.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_name", nullable = false, length = 200)
    private String reportName;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_format", nullable = false)
    private FileFormat fileFormat;

    @Column(name = "generated_by", nullable = false)
    private Long generatedBy;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "JSON")
    private String parameters;
}