package com.urbanpulse.infrastructure.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Sensor reading entity storing time-series IoT data.
 * Optimized for high-volume ingestion with partitioning support.
 */
@Entity
@Table(name = "sensor_readings", indexes = {
    @Index(name = "idx_readings_sensor_time", columnList = "sensor_id,recorded_at"),
    @Index(name = "idx_readings_recorded_at", columnList = "recorded_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sensor_id", nullable = false)
    private UUID sensorId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "value", nullable = false, precision = 18, scale = 6)
    private Double value;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "quality_score")
    private Integer qualityScore;

    @Column(name = "is_anomaly")
    private Boolean isAnomaly;

    @Column(name = "anomaly_score")
    private Double anomalyScore;

    @Column(name = "raw_data", columnDefinition = "jsonb")
    private String rawData;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @CreationTimestamp
    @Column(name = "ingested_at", nullable = false, updatable = false)
    private LocalDateTime ingestedAt;

    @PrePersist
    public void prePersist() {
        if (recordedAt == null) recordedAt = LocalDateTime.now();
        if (qualityScore == null) qualityScore = 100;
    }
}
