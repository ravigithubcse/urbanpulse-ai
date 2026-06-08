package com.urbanpulse.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UrbanPulse AI - API Gateway
 * 
 * Spring Cloud Gateway providing routing, load balancing, rate limiting,
 * circuit breaking, and cross-cutting concerns for all microservices.
 * 
 * @author Ravi Kumar
 * @version 1.0.0
 */
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}