package com.gatewayApi.config;

import com.gatewayApi.config.security.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFilter filter;

    @Bean
    public RouteLocator routesLocator(RouteLocatorBuilder routeBuilder) {
        return routeBuilder
            .routes()
            .route("auth-service", r ->
                r
                    .path("/auth/**")
                    .filters(f -> f.filter(this.filter))
                    .uri("lb://authentication-service")
            )
            .route("user-managment-service", r ->
                r
                    .path("/user/**")
                    .filters(f -> f.filter(this.filter))
                    .uri("lb://USER-MANAGMENT-SERVICE")
            )
            .route("wallet-service", r ->
                r
                    .path("/wallet/**")
                    .filters(f -> f.filter(this.filter))
                    .uri("lb://WALLET-SERVICE")
            )
            .route("history-service", r ->
                r
                    .path("/history/**")
                    .filters(f -> f.filter(this.filter))
                    .uri("lb://HISTORY-SERVICE")
            )
            .route("notification-service", r ->
                r
                    .path("/notification/**")
                    .filters(f -> f.filter(this.filter))
                    .uri("lb://USER-MANAGMENT-SERVICE")
            )
            .route("config-service", r ->
                r
                    .path("/config/**")
                    .filters(f -> f.filter(this.filter))
                    .uri("lb://config-service")
            )
            .route("transaction-service", r ->
                r
                    .path("/transaction/**")
                    .filters(f -> f.filter(this.filter))
                    .uri("lb://transaction-service")
            )
            .build();
    }
}
