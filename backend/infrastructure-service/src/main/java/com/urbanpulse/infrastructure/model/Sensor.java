package com.urbanpulse.infrastructure.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * IoT Sensor entity attached to infrastructure assets.
 * Supports multiple protocols: MQTT, HTTP, WebSocket.
 */
@Entity
@Table(name = "sensors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "sensor_code", nullable = false, unique = true, length = 50)
    private String sensorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "sensor_type", nullable = false, length = 50)
    private String sensorType;

    @Column(name = "protocol", nullable = false, length = 20)
    private String protocol;

    @Column(name = "mqtt_topic", length = 200)
    private String mqttTopic;

    @Column(name = "http_endpoint", length = 500)
    private String httpEndpoint;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "min_value")
    private Double minValue;

    @Column(name = "max_value")
    private Double maxValue;

    @Column(name = "threshold_warning")
    private Double thresholdWarning;

    @Column(name = "threshold_critical")
    private Double thresholdCritical;

    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "firmware_version", length = 50)
    private String firmwareVersion;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "last_reading_at")
    private LocalDateTime lastReadingAt;

    @Column(name = "last_reading_value")
    private Double lastReadingValue;

    @Column(name = "reading_count")
    private Long readingCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (isActive == null) isActive = true;
        if (readingCount == null) readingCount = 0L;
    }
}
