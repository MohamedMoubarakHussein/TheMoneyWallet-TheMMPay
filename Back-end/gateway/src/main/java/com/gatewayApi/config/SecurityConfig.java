package com.gatewayApi.config;

import com.gatewayApi.config.security.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFilter filter;
    private final RedisRateLimiter redisRateLimiter;

    @Bean
    public RouteLocator routesLocator(RouteLocatorBuilder routeBuilder) {
        return routeBuilder
            .routes()
            .route("auth-service", r ->
                r
                    .path("/auth/**")
                    .filters(f -> f
                            .filter(this.filter)
                            .circuitBreaker(cb -> cb.setName("authServiceCb").setFallbackUri("forward:/fallback"))
                            .requestRateLimiter(config -> config
                                    .setRateLimiter(redisRateLimiter)
                                    .setStatusCode(HttpStatus.TOO_MANY_REQUESTS))
                    )
                    .uri("lb://authentication-service")
            )
            .route("user-managment-service", r ->
                r
                    .path("/user/**")
                    .filters(f -> f.filter(this.filter)
                            .circuitBreaker(cb -> cb.setName("userServiceCb").setFallbackUri("forward:/fallback"))
                            .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter).setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                    .uri("lb://USER-MANAGMENT-SERVICE")
            )
            .route("wallet-service", r ->
                r
                    .path("/wallet/**")
                    .filters(f -> f.filter(this.filter)
                            .circuitBreaker(cb -> cb.setName("walletServiceCb").setFallbackUri("forward:/fallback"))
                            .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter).setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                    .uri("lb://WALLET-SERVICE")
            )
            .route("history-service", r ->
                r
                    .path("/history/**")
                    .filters(f -> f.filter(this.filter)
                            .circuitBreaker(cb -> cb.setName("historyServiceCb").setFallbackUri("forward:/fallback"))
                            .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter).setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                    .uri("lb://HISTORY-SERVICE")
            )
            .route("notification-service", r ->
                r
                    .path("/notification/**")
                    .filters(f -> f.filter(this.filter)
                            .circuitBreaker(cb -> cb.setName("notificationServiceCb").setFallbackUri("forward:/fallback"))
                            .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter).setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                    .uri("lb://USER-MANAGMENT-SERVICE")
            )
            .route("config-service", r ->
                r
                    .path("/config/**")
                    .filters(f -> f.filter(this.filter)
                            .circuitBreaker(cb -> cb.setName("configServiceCb").setFallbackUri("forward:/fallback"))
                            .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter).setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                    .uri("lb://config-service")
            )
            .route("transaction-service", r ->
                r
                    .path("/transaction/**")
                    .filters(f -> f.filter(this.filter)
                            .circuitBreaker(cb -> cb.setName("transactionServiceCb").setFallbackUri("forward:/fallback"))
                            .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter).setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                    .uri("lb://transaction-service")
            ).route("dashboard-service", r ->
                r
                    .path("/dashboard/**")
                    .filters(f -> f.filter(this.filter)
                            .circuitBreaker(cb -> cb.setName("dashboardServiceCb").setFallbackUri("forward:/fallback"))
                            .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter).setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                    .uri("lb://dashboard-service")
            )
            .build();
    }
}
