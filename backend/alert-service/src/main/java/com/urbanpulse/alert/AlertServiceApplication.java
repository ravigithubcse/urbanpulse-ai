package com.urbanpulse.alert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UrbanPulse AI - Alert Service
 * 
 * Intelligent alerting system that processes predictions, classifies severity,
 * manages notification channels, and handles alert lifecycle.
 * 
 * @author Ravi Kumar
 * @version 1.0.0
 */
@SpringBootApplication
public class AlertServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlertServiceApplication.class, args);
    }
}