package com.gatewayApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableDiscoveryClient
@EnableWebFlux
public class Gateway {
    //TODO  add ciruit breaker , rate limiting
    public static void main(String[] args) {
        SpringApplication.run(Gateway.class, args);
    }
}
