package com.urbanpulse.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * UrbanPulse AI - Authentication Service
 * 
 * Handles user authentication, authorization, JWT token management,
 * and session storage via Redis.
 * 
 * @author Ravi Kumar
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
