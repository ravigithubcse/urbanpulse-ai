package com.urbanpulse.infrastructure.repository;

import com.urbanpulse.infrastructure.model.SensorReading;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for SensorReading entity with time-series query capabilities.
 */
@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    List<SensorReading> findBySensorIdOrderByRecordedAtDesc(UUID sensorId, Pageable pageable);

    List<SensorReading> findByAssetIdOrderByRecordedAtDesc(UUID assetId, Pageable pageable);

    @Query("SELECT sr FROM SensorReading sr WHERE sr.sensorId = :sensorId AND sr.recordedAt BETWEEN :start AND :end ORDER BY sr.recordedAt")
    List<SensorReading> findBySensorIdAndTimeRange(@Param("sensorId") UUID sensorId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT AVG(sr.value), MIN(sr.value), MAX(sr.value), STDDEV(sr.value), COUNT(sr) " +
           "FROM SensorReading sr WHERE sr.sensorId = :sensorId AND sr.recordedAt >= :since")
    List<Object[]> getStatistics(@Param("sensorId") UUID sensorId, @Param("since") LocalDateTime since);

    @Query("SELECT sr.recordedAt, sr.value FROM SensorReading sr WHERE sr.sensorId = :sensorId AND sr.recordedAt >= :since ORDER BY sr.recordedAt")
    List<Object[]> getTimeSeries(@Param("sensorId") UUID sensorId, @Param("since") LocalDateTime since);

    long countBySensorId(UUID sensorId);

    @Query("SELECT sr FROM SensorReading sr WHERE sr.isAnomaly = true AND sr.assetId = :assetId ORDER BY sr.recordedAt DESC")
    List<SensorReading> findAnomaliesByAsset(@Param("assetId") UUID assetId, Pageable pageable);
}
