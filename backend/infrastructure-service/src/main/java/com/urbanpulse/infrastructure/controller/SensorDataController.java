package com.urbanpulse.infrastructure.controller;

import com.urbanpulse.infrastructure.model.SensorReading;
import com.urbanpulse.infrastructure.service.SensorDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/infrastructure/readings")
@RequiredArgsConstructor
@Tag(name = "Sensor Data", description = "IoT sensor data ingestion and query APIs")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    @PostMapping
    @Operation(summary = "Ingest sensor reading")
    public ResponseEntity<SensorReading> ingestReading(@RequestBody SensorReading reading) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorDataService.ingestReading(reading));
    }

    @PostMapping("/batch")
    @Operation(summary = "Batch ingest sensor readings")
    public ResponseEntity<List<SensorReading>> batchIngest(@RequestBody List<SensorReading> readings) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorDataService.batchIngest(readings));
    }

    @GetMapping("/sensor/{sensorId}")
    @Operation(summary = "Get recent readings for a sensor")
    public ResponseEntity<List<SensorReading>> getRecentReadings(
            @PathVariable UUID sensorId,
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(sensorDataService.getRecentReadings(sensorId, limit));
    }

    @GetMapping("/sensor/{sensorId}/range")
    @Operation(summary = "Get readings for a sensor within time range")
    public ResponseEntity<List<SensorReading>> getReadingsByTimeRange(
            @PathVariable UUID sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(sensorDataService.getReadingsByTimeRange(sensorId, start, end));
    }

    @GetMapping("/sensor/{sensorId}/statistics")
    @Operation(summary = "Get sensor statistics since a time")
    public ResponseEntity<Map<String, Object>> getSensorStatistics(
            @PathVariable UUID sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        return ResponseEntity.ok(sensorDataService.getSensorStatistics(sensorId, since));
    }

    @GetMapping("/sensor/{sensorId}/timeseries")
    @Operation(summary = "Get time series data for a sensor")
    public ResponseEntity<List<Map<String, Object>>> getTimeSeries(
            @PathVariable UUID sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        return ResponseEntity.ok(sensorDataService.getTimeSeries(sensorId, since));
    }
}
