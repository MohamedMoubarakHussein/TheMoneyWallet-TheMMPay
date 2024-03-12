package com.gatewayApi.config.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.gatewayApi.Service.HttpService;
import com.gatewayApi.Service.OpenedAndSecuredPathsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GatewayFilter {

    private final OpenedAndSecuredPathsService openedAndSecuredPathsService;
    private final HttpService httpService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (openedAndSecuredPathsService.isSecuredEndPoint(request)) {
            if (!request.getHeaders().containsKey("Authorization")) {
                ServerHttpResponse re = exchange.getResponse();
                re.setStatusCode(HttpStatus.UNAUTHORIZED);
                return re.setComplete();
            }

            @SuppressWarnings("null")
            String token = request.getHeaders().get("Authorization").get(0).substring(7);
            boolean isValid = this.httpService.isTokenValid(token);
            
            if (!isValid) {
                ServerHttpResponse re = exchange.getResponse();
                re.setStatusCode(HttpStatus.UNAUTHORIZED);
                return re.setComplete();
            }

        }

        return chain.filter(exchange);

    }

}
