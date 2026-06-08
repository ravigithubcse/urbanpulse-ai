package com.urbanpulse.alert.controller;

import com.urbanpulse.alert.model.Alert;
import com.urbanpulse.alert.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Alert management APIs")
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/active")
    @Operation(summary = "Get active alerts")
    public ResponseEntity<Page<Alert>> getActiveAlerts(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(alertService.getActiveAlerts(pageable));
    }

    @GetMapping("/severity/{severity}")
    @Operation(summary = "Get alerts by severity")
    public ResponseEntity<Page<Alert>> getAlertsBySeverity(
            @PathVariable Alert.Severity severity,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(alertService.getAlertsBySeverity(severity, pageable));
    }

    @PostMapping("/{alertId}/acknowledge")
    @Operation(summary = "Acknowledge an alert")
    public ResponseEntity<Map<String, String>> acknowledgeAlert(
            @PathVariable UUID alertId,
            @RequestParam UUID userId,
            @RequestParam(required = false) String notes) {
        alertService.acknowledgeAlert(alertId, userId, notes);
        return ResponseEntity.ok(Map.of("message", "Alert acknowledged"));
    }

    @PostMapping("/{alertId}/resolve")
    @Operation(summary = "Resolve an alert")
    public ResponseEntity<Map<String, String>> resolveAlert(
            @PathVariable UUID alertId,
            @RequestParam UUID userId,
            @RequestParam(required = false) String notes) {
        alertService.resolveAlert(alertId, userId, notes);
        return ResponseEntity.ok(Map.of("message", "Alert resolved"));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get alert statistics")
    public ResponseEntity<Map<String, Object>> getAlertStats() {
        return ResponseEntity.ok(alertService.getAlertStats());
    }
}