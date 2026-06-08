package com.urbanpulse.alert.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "prediction_id")
    private UUID predictionId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "asset_name", length = 200)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AlertStatus status;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "predicted_failure_at")
    private LocalDateTime predictedFailureAt;

    @Column(name = "acknowledged_by")
    private UUID acknowledgedBy;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "acknowledgment_notes", length = 1000)
    private String acknowledgmentNotes;

    @Column(name = "resolved_by")
    private UUID resolvedBy;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolution_notes", length = 1000)
    private String resolutionNotes;

    @Column(name = "escalation_level")
    private Integer escalationLevel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Severity { CRITICAL, HIGH, MEDIUM, LOW, INFO }
    public enum AlertType { PREDICTED_FAILURE, ANOMALY_DETECTED, THRESHOLD_BREACH, SENSOR_OFFLINE, MAINTENANCE_DUE }
    public enum AlertStatus { ACTIVE, ACKNOWLEDGED, ESCALATED, RESOLVED, SUPPRESSED }
}