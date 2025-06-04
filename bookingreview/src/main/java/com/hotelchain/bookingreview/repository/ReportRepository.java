package com.hotelchain.bookingreview.repository;

import com.hotelchain.bookingreview.entity.Report;
import com.hotelchain.bookingreview.entity.ReportType;
import com.hotelchain.bookingreview.entity.FileFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReportType(ReportType reportType);
    List<Report> findByFileFormat(FileFormat fileFormat);
    List<Report> findByGeneratedBy(Long generatedBy);
    List<Report> findByReportTypeAndGeneratedBy(ReportType reportType, Long generatedBy);
}