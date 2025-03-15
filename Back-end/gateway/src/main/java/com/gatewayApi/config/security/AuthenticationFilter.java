package com.gatewayApi.config.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
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

/**
 * Main filter for see if the end point is secure or not 
 *  we have the filter here that intercpt the request 
 *  and check first if the token in the header if not 
 *   it checks the cookie  
 *   we use two servicess http service for checking if token is vaild
 *   and the second that define what open and closed  end points 
 */
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
            
            String token = null;
            if (request.getHeaders().containsKey("Authorization") && request.getHeaders().get("Authorization").get(0).startsWith("Bearer ")) {
                token = request.getHeaders().get("Authorization").get(0).substring(7);
            }else{
                HttpCookie authCookie = exchange.getRequest().getCookies().getFirst("Authorization");
                if (authCookie != null && !authCookie.getValue().isEmpty()) {
                    String cookieValue = authCookie.getValue();
                  
                    token = cookieValue.startsWith("Bearer ") ? 
                            cookieValue.substring(7) : cookieValue;
                }
            }

            
           
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
