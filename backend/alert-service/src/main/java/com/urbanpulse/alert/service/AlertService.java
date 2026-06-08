package com.urbanpulse.alert.service;

import com.urbanpulse.alert.model.Alert;
import com.urbanpulse.alert.repository.AlertRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "predictions.generated", groupId = "alert-service")
    @Transactional
    public void handlePredictionEvent(String eventJson) {
        try {
            Map<String, Object> event = objectMapper.readValue(eventJson, Map.class);
            String assetId = (String) event.get("assetId");
            Double probability = ((Number) event.get("probability")).doubleValue();
            String modelVersion = (String) event.get("modelVersion");

            Alert.Severity severity = classifySeverity(probability);
            Alert.AlertType alertType = Alert.AlertType.PREDICTED_FAILURE;

            Alert alert = Alert.builder()
                    .predictionId(UUID.fromString((String) event.get("predictionId")))
                    .assetId(UUID.fromString(assetId))
                    .assetName((String) event.getOrDefault("assetName", "Unknown Asset"))
                    .severity(severity)
                    .alertType(alertType)
                    .title(generateAlertTitle(severity, alertType, (String) event.getOrDefault("assetName", "Unknown")))
                    .description(generateAlertDescription(event, probability))
                    .status(Alert.AlertStatus.ACTIVE)
                    .confidenceScore(probability)
                    .predictedFailureAt(LocalDateTime.parse((String) event.get("predictedWindowEnd")))
                    .escalationLevel(0)
                    .build();

            Alert saved = alertRepository.save(alert);

            // Cache active alert
            redisTemplate.opsForZSet().add("alerts:active", saved.getId().toString(), 
                    saved.getCreatedAt().toEpochSecond(java.time.ZoneOffset.UTC));

            // Send WebSocket notification
            messagingTemplate.convertAndSend("/topic/alerts", saved);

            // Publish notification event
            publishNotificationEvent(saved);

            log.info("Alert created: {} for asset {} with severity {}", saved.getId(), assetId, severity);

        } catch (Exception e) {
            log.error("Error processing prediction event: {}", e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Alert> getActiveAlerts(Pageable pageable) {
        return alertRepository.findByStatusOrderByCreatedAtDesc(Alert.AlertStatus.ACTIVE, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Alert> getAlertsBySeverity(Alert.Severity severity, Pageable pageable) {
        return alertRepository.findBySeverityOrderByCreatedAtDesc(severity, pageable);
    }

    @Transactional
    public void acknowledgeAlert(UUID alertId, UUID userId, String notes) {
        alertRepository.acknowledgeAlert(alertId, userId, LocalDateTime.now(), notes, Alert.AlertStatus.ACKNOWLEDGED);
        redisTemplate.opsForZSet().remove("alerts:active", alertId.toString());
        log.info("Alert {} acknowledged by user {}", alertId, userId);
    }

    @Transactional
    public void resolveAlert(UUID alertId, UUID userId, String notes) {
        alertRepository.resolveAlert(alertId, userId, LocalDateTime.now(), notes, Alert.AlertStatus.RESOLVED);
        redisTemplate.opsForZSet().remove("alerts:active", alertId.toString());
        log.info("Alert {} resolved by user {}", alertId, userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAlertStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        stats.put("totalActive", alertRepository.countByStatus(Alert.AlertStatus.ACTIVE));
        stats.put("severityDistribution", alertRepository.countBySeveritySince(since));
        stats.put("statusDistribution", alertRepository.countByStatus());
        stats.put("typeDistribution", alertRepository.countByTypeSince(since));
        stats.put("timestamp", LocalDateTime.now());
        return stats;
    }

    private Alert.Severity classifySeverity(double probability) {
        if (probability >= 0.9) return Alert.Severity.CRITICAL;
        if (probability >= 0.75) return Alert.Severity.HIGH;
        if (probability >= 0.5) return Alert.Severity.MEDIUM;
        if (probability >= 0.25) return Alert.Severity.LOW;
        return Alert.Severity.INFO;
    }

    private String generateAlertTitle(Alert.Severity severity, Alert.AlertType type, String assetName) {
        return String.format("[%s] %s detected for %s", severity, type.toString().replace("_", " "), assetName);
    }

    private String generateAlertDescription(Map<String, Object> event, double probability) {
        return String.format("Predicted failure probability: %.1f%%. Model: %s. Contributing factors: %s",
                probability * 100,
                event.get("modelVersion"),
                event.getOrDefault("contributingFactors", "N/A"));
    }

    @SneakyThrows
    private void publishNotificationEvent(Alert alert) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ALERT_NOTIFICATION");
        notification.put("alertId", alert.getId().toString());
        notification.put("severity", alert.getSeverity().toString());
        notification.put("title", alert.getTitle());
        notification.put("channels", Arrays.asList("WEBSOCKET", "EMAIL"));
        kafkaTemplate.send("notifications.send", objectMapper.writeValueAsString(notification));
    }
}