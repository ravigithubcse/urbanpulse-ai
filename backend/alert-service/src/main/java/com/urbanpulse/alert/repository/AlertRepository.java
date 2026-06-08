package com.urbanpulse.alert.repository;

import com.urbanpulse.alert.model.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {

    Page<Alert> findByStatusOrderByCreatedAtDesc(Alert.AlertStatus status, Pageable pageable);

    Page<Alert> findBySeverityOrderByCreatedAtDesc(Alert.Severity severity, Pageable pageable);

    Page<Alert> findByAssetIdOrderByCreatedAtDesc(UUID assetId, Pageable pageable);

    List<Alert> findByStatusAndCreatedAtBefore(Alert.AlertStatus status, LocalDateTime time);

    @Query("SELECT a.severity, COUNT(a) FROM Alert a WHERE a.createdAt >= :since GROUP BY a.severity")
    List<Object[]> countBySeveritySince(@Param("since") LocalDateTime since);

    @Query("SELECT a.status, COUNT(a) FROM Alert a GROUP BY a.status")
    List<Object[]> countByStatus();

    @Modifying
    @Query("UPDATE Alert a SET a.status = :status, a.acknowledgedBy = :userId, a.acknowledgedAt = :time, a.acknowledgmentNotes = :notes WHERE a.id = :alertId")
    void acknowledgeAlert(@Param("alertId") UUID alertId, @Param("userId") UUID userId, @Param("time") LocalDateTime time, @Param("notes") String notes, @Param("status") Alert.AlertStatus status);

    @Modifying
    @Query("UPDATE Alert a SET a.status = :status, a.resolvedBy = :userId, a.resolvedAt = :time, a.resolutionNotes = :notes WHERE a.id = :alertId")
    void resolveAlert(@Param("alertId") UUID alertId, @Param("userId") UUID userId, @Param("time") LocalDateTime time, @Param("notes") String notes, @Param("status") Alert.AlertStatus status);

    long countByStatus(Alert.AlertStatus status);

    @Query("SELECT a.alertType, COUNT(a) FROM Alert a WHERE a.createdAt >= :since GROUP BY a.alertType")
    List<Object[]> countByTypeSince(@Param("since") LocalDateTime since);
}