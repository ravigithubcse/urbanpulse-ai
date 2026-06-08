package com.urbanpulse.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UrbanPulse AI - Infrastructure Service
 * 
 * Core service for managing infrastructure assets, sensors, and real-time telemetry data.
 * Provides CRUD operations, geospatial queries, sensor data ingestion, and
 * publishes events to Kafka for downstream processing.
 * 
 * @author Ravi Kumar
 * @version 1.0.0
 */
@SpringBootApplication
public class InfrastructureServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfrastructureServiceApplication.class, args);
    }
}
