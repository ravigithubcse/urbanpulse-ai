package com.urbanpulse.infrastructure.repository;

import com.urbanpulse.infrastructure.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Sensor entity operations.
 */
@Repository
public interface SensorRepository extends JpaRepository<Sensor, UUID> {

    Optional<Sensor> findBySensorCode(String sensorCode);

    List<Sensor> findByAssetId(UUID assetId);

    List<Sensor> findByIsActiveTrue();

    List<Sensor> findByProtocol(String protocol);

    long countByIsActiveTrue();

    @Modifying
    @Query("UPDATE Sensor s SET s.lastReadingAt = :time, s.lastReadingValue = :value, s.readingCount = COALESCE(s.readingCount, 0) + 1 WHERE s.id = :sensorId")
    void updateLastReading(@Param("sensorId") UUID sensorId, @Param("value") Double value, @Param("time") LocalDateTime time);

    @Query("SELECT s.sensorType, COUNT(s) FROM Sensor s WHERE s.isActive = true GROUP BY s.sensorType")
    List<Object[]> countBySensorType();
}
