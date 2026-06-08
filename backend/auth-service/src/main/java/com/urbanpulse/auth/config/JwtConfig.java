package com.urbanpulse.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * JWT configuration properties loaded from application.yml
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {

    private String secret;
    private Duration accessTokenExpiration = Duration.ofMinutes(30);
    private Duration refreshTokenExpiration = Duration.ofDays(7);
    private String issuer = "urbanpulse-ai";
    private String audience = "urbanpulse-client";
}
