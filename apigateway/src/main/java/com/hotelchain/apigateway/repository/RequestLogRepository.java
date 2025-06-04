package com.hotelchain.apigateway.repository;

import com.hotelchain.apigateway.entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
    List<RequestLog> findBySessionId(String sessionId);
    List<RequestLog> findByUserId(Long userId);
    List<RequestLog> findByEndpoint(String endpoint);
    List<RequestLog> findByStatusCode(Integer statusCode);

    @Query("SELECT r FROM RequestLog r WHERE r.timestamp BETWEEN :startTime AND :endTime")
    List<RequestLog> findByTimestampBetween(@Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT AVG(r.responseTimeMs) FROM RequestLog r WHERE r.endpoint = :endpoint")
    Double getAverageResponseTimeByEndpoint(@Param("endpoint") String endpoint);
}
