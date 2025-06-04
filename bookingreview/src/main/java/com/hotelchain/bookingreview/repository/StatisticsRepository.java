package com.hotelchain.bookingreview.repository;

import com.hotelchain.bookingreview.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    List<Statistics> findByMetricName(String metricName);
    List<Statistics> findByHotelId(Long hotelId);
    List<Statistics> findByRoomId(Long roomId);
    List<Statistics> findByMetricDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM Statistics s WHERE s.metricName = :metricName AND s.metricDate BETWEEN :startDate AND :endDate")
    List<Statistics> findByMetricNameAndDateRange(@Param("metricName") String metricName,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
}