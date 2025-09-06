package com.gatewayapi.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.gatewayapi.Service.OpenedAndSecuredPathsService;
import com.gatewayapi.Service.shared.JwtValidator;

import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GatewayFilter {

    private final OpenedAndSecuredPathsService openedAndSecuredPathsService;
    private final JwtValidator jwtValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (openedAndSecuredPathsService.isSecuredEndPoint(request)) {
            String token = request.getHeaders().get("Authorization").get(0).substring(7);
            boolean validRequest = request.getHeaders().containsKey("Authorization") &&request.getHeaders().get("Authorization").get(0).startsWith("Bearer ") && this.jwtValidator.isTokenValid(token);
            if (!validRequest) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                String body = "{\"error\": \"Authentication Failed\"}";
                DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());

                return response.writeWith(Mono.just(buffer));
            }
        }

        return chain.filter(exchange);
    }
}
