package com.urbanpulse.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UrbanPulse AI - Analytics Service
 * 
 * Provides analytics, reporting, and search capabilities using Elasticsearch.
 * Calculates cost avoidance, model performance metrics, and generates reports.
 * 
 * @author Ravi Kumar
 * @version 1.0.0
 */
@SpringBootApplication
public class AnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }
}