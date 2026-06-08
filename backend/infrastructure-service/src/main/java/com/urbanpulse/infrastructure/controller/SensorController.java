package com.urbanpulse.infrastructure.controller;

import com.urbanpulse.infrastructure.model.Sensor;
import com.urbanpulse.infrastructure.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/infrastructure/sensors")
@RequiredArgsConstructor
@Tag(name = "Sensors", description = "IoT sensor management APIs")
public class SensorController {

    private final SensorService sensorService;

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get sensors by asset ID")
    public ResponseEntity<List<Sensor>> getSensorsByAsset(@PathVariable UUID assetId) {
        return ResponseEntity.ok(sensorService.getSensorsByAsset(assetId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sensor by ID")
    public ResponseEntity<Sensor> getSensorById(@PathVariable UUID id) {
        return ResponseEntity.ok(sensorService.getSensorById(id));
    }

    @PostMapping
    @Operation(summary = "Create sensor")
    public ResponseEntity<Sensor> createSensor(@RequestBody Sensor sensor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorService.createSensor(sensor));
    }

    @GetMapping("/stats/count")
    @Operation(summary = "Get active sensor count")
    public ResponseEntity<Map<String, Long>> getSensorCount() {
        return ResponseEntity.ok(Map.of("count", sensorService.getActiveSensorCount()));
    }

    @GetMapping("/stats/types")
    @Operation(summary = "Get sensor type distribution")
    public ResponseEntity<List<Object[]>> getSensorTypeDistribution() {
        return ResponseEntity.ok(sensorService.getSensorTypeDistribution());
    }
}
