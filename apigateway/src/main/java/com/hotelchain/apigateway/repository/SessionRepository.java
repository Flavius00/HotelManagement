package com.hotelchain.apigateway.repository;

import com.hotelchain.apigateway.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    List<Session> findByUserId(Long userId);
    List<Session> findByIsActive(Boolean isActive);
    Optional<Session> findByIdAndIsActive(String id, Boolean isActive);

    @Query("SELECT s FROM Session s WHERE s.expiresAt < :now")
    List<Session> findExpiredSessions(@Param("now") LocalDateTime now);

    @Query("DELETE FROM Session s WHERE s.expiresAt < :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);
}
