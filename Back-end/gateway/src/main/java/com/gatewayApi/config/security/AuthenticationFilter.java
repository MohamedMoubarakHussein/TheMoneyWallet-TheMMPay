package com.gatewayApi.config.security;

import com.gatewayApi.Service.OpenedAndSecuredPathsService;
import com.gatewayApi.Service.shared.JwtValidator;
import java.util.Map;
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
    private final JwtValidator jwtValidator;
    private int statusOfValidation = 0;

    @Override
    public Mono<Void> filter(
        ServerWebExchange exchange,
        GatewayFilterChain chain
    ) {
        ServerHttpRequest request = exchange.getRequest();
        if (openedAndSecuredPathsService.isSecuredEndPoint(request)) {
            String token = null;
            String oldTok = null;
            if (
                request.getHeaders().containsKey("Authorization") &&
                request
                    .getHeaders()
                    .get("Authorization")
                    .get(0)
                    .startsWith("Bearer ")
            ) {
                oldTok = request
                    .getHeaders()
                    .get("Authorization")
                    .get(0);
                token = oldTok
                    .substring(7);
            }

          
            Map<Integer, Object> isValid =
                this.jwtValidator.isTokenValidVerbose(token);
            if (!Boolean.valueOf((String) isValid.get(1))) {
                ServerHttpResponse re = exchange.getResponse();
                re.setStatusCode(HttpStatus.UNAUTHORIZED);
                re.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                String errorMessage = this.jwtValidator.msg != null ? 
                    this.jwtValidator.msg : "Authentication failed";
                String body = "{\"error\": \"" + errorMessage + "\", \"timestamp\": \"" + 
                    java.time.Instant.now() + "\"}";

                DataBuffer buffer = re.bufferFactory().wrap(body.getBytes());

                return re.writeWith(Mono.just(buffer));
            } else {
                log.debug("Authentication successful with token: {}", token);
                ServerHttpRequest mutatedRequest = request
                    .mutate()
                    .header("x-Authorization", token)
                    .header(
                        "Authorization",
                         token
                    )
                    .build();

                ServerWebExchange mutatedExchange = exchange
                    .mutate()
                    .request(mutatedRequest)
                    .build();

                return chain.filter(mutatedExchange);
            }
        }

        return chain.filter(exchange);
    }
}
