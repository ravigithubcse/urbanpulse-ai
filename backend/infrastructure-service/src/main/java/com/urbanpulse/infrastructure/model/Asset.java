package com.urbanpulse.infrastructure.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Infrastructure asset entity representing physical urban infrastructure.
 * Supports roads, bridges, water pipes, power lines, and telecom towers.
 * Includes geospatial location using PostGIS.
 */
@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "asset_code", nullable = false, unique = true, length = 50)
    private String assetCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_type_id", nullable = false)
    private AssetType assetType;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(name = "health_score")
    private Integer healthScore;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssetStatus status;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "next_scheduled_maintenance")
    private LocalDate nextScheduledMaintenance;

    @Column(name = "material", length = 100)
    private String material;

    @Column(name = "age_years")
    private Integer ageYears;

    @Column(name = "length_meters")
    private Double lengthMeters;

    @Column(name = "diameter_mm")
    private Double diameterMm;

    @Column(name = "capacity")
    private Double capacity;

    @Column(name = "manufacturer", length = 200)
    private String manufacturer;

    @Column(name = "model", length = 200)
    private String model;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Sensor> sensors = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public enum AssetStatus {
        ACTIVE,
        INACTIVE,
        UNDER_MAINTENANCE,
        DECOMMISSIONED,
        FAILED
    }

    @PrePersist
    public void prePersist() {
        if (status == null) status = AssetStatus.ACTIVE;
        if (healthScore == null) healthScore = 100;
        if (ageYears == null && installationDate != null) {
            ageYears = LocalDate.now().getYear() - installationDate.getYear();
        }
    }
}
