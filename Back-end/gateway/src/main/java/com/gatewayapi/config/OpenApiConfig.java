package com.gatewayapi.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
    private final RouteDefinitionLocator locator;
    @Bean
    public List<GroupedOpenApi> apis() {
        return locator.getRouteDefinitions().collectList().block()
                .stream()
                .filter(route -> route.getId().endsWith("-service"))
                .map(route -> {
                    String name = route.getId().replace("-service", "");
                    return GroupedOpenApi.builder()
                            .group(name)
                            .pathsToMatch("/" + name + "/**")
                            .build();
                })
                .collect(Collectors.toList());
    }
}
