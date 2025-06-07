package com.gatewayApi.Service;

import java.util.List;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class OpenedAndSecuredPathsService {

    private static final List<String> openEndPoints = List.of(
        "/auth/signup",
        "/auth/signin",
        "/auth/logout",
        "/auth/refreshtoken",
        "/auth/v3/api-docs",
        "/auth/v3/api-docs/**",
        "/auth/swagger-ui.html",
        "/auth/swagger-ui/index.html",
        "/auth/swagger-ui/**",
        "/auth/webjars/**"
    );

    public Boolean isSecuredEndPoint(ServerHttpRequest request) {
        return OpenedAndSecuredPathsService.openEndPoints
            .stream()
            .noneMatch(url -> request.getURI().getPath().contains(url));
    }
}
