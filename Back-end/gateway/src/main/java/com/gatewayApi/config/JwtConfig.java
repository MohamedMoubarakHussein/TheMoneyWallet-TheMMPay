package com.gatewayApi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
@Data
public class JwtConfig {
    private String secretKey;
}
