package com.urbanpulse.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        return buildFallbackResponse("Auth Service");
    }

    @RequestMapping("/fallback/infrastructure")
    public ResponseEntity<Map<String, Object>> infrastructureFallback() {
        return buildFallbackResponse("Infrastructure Service");
    }

    @RequestMapping("/fallback/alerts")
    public ResponseEntity<Map<String, Object>> alertsFallback() {
        return buildFallbackResponse("Alert Service");
    }

    @RequestMapping("/fallback/analytics")
    public ResponseEntity<Map<String, Object>> analyticsFallback() {
        return buildFallbackResponse("Analytics Service");
    }

    @RequestMapping("/fallback/predictions")
    public ResponseEntity<Map<String, Object>> predictionsFallback() {
        return buildFallbackResponse("Prediction Service");
    }

    private ResponseEntity<Map<String, Object>> buildFallbackResponse(String serviceName) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", serviceName + " is temporarily unavailable. Please try again later.");
        response.put("service", serviceName);
        response.put("retryAfter", 30);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}