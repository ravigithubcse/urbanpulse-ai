package com.urbanpulse.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Auth Service Routes
            .route("auth-service", r -> r.path("/api/v1/auth/**", "/api/v1/users/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .circuitBreaker(config -> config
                        .setName("authCircuitBreaker")
                        .setFallbackUri("forward:/fallback/auth"))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(exchange -> 
                            reactor.core.publisher.Mono.just(
                                exchange.getRequest().getRemoteAddress() != null 
                                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
                                    : "unknown")))
                    .retry(retryConfig -> retryConfig.setRetries(3)))
                .uri("http://auth-service:8082"))

            // Infrastructure Service Routes
            .route("infrastructure-service", r -> r.path("/api/v1/infrastructure/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .circuitBreaker(config -> config
                        .setName("infraCircuitBreaker")
                        .setFallbackUri("forward:/fallback/infrastructure"))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(exchange -> 
                            reactor.core.publisher.Mono.just(
                                exchange.getRequest().getRemoteAddress() != null 
                                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
                                    : "unknown"))))
                .uri("http://infrastructure-service:8083"))

            // Alert Service Routes
            .route("alert-service", r -> r.path("/api/v1/alerts/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .circuitBreaker(config -> config
                        .setName("alertCircuitBreaker")
                        .setFallbackUri("forward:/fallback/alerts"))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(exchange -> 
                            reactor.core.publisher.Mono.just(
                                exchange.getRequest().getRemoteAddress() != null 
                                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
                                    : "unknown"))))
                .uri("http://alert-service:8084"))

            // Analytics Service Routes
            .route("analytics-service", r -> r.path("/api/v1/analytics/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .circuitBreaker(config -> config
                        .setName("analyticsCircuitBreaker")
                        .setFallbackUri("forward:/fallback/analytics"))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(exchange -> 
                            reactor.core.publisher.Mono.just(
                                exchange.getRequest().getRemoteAddress() != null 
                                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
                                    : "unknown"))))
                .uri("http://analytics-service:8085"))

            // Prediction Service Routes
            .route("prediction-service", r -> r.path("/api/v1/predictions/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .circuitBreaker(config -> config
                        .setName("predictionCircuitBreaker")
                        .setFallbackUri("forward:/fallback/predictions"))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(exchange -> 
                            reactor.core.publisher.Mono.just(
                                exchange.getRequest().getRemoteAddress() != null 
                                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
                                    : "unknown"))))
                .uri("http://prediction-service:8000"))

            // WebSocket Routes
            .route("websocket-alerts", r -> r.path("/ws/**")
                .uri("ws://alert-service:8084"))

            .build();
    }

    @Bean
    public org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter redisRateLimiter() {
        return new org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter(100, 200, 1);
    }
}