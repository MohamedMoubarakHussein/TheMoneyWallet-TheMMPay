package com.gatewayApi.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gatewayApi.config.security.AuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  
  private final AuthenticationFilter filter;

  @Bean
  public RouteLocator routesLocator(RouteLocatorBuilder routeBuilder) {
    return routeBuilder.routes()
        .route("auth-service",
            r -> r.path("/auth/**").filters(f -> f.filter(this.filter)).uri("lb://authentication-service"))
        .route("user-managment-service",
            r -> r.path("/user/**").filters(f -> f.filter(this.filter)).uri("lb://USER-MANAGMENT-SERVICE"))
        .build();
  }

}
