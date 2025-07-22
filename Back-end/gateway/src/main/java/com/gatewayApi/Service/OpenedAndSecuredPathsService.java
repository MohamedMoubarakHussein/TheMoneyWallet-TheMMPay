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
        "/user/v3/api-docs",
        "/dashboard/v3/api-docs",
        "/history/v3/api-docs",
        "/notification/v3/api-docs",
        "/transaction/v3/api-docs",
        "/wallet/v3/api-docs",

        "/auth/oauth2/**",
        "/auth/oauth2/google",
        
        "/transaction/ws",
        "/transaction/ws/**"
    );

    public Boolean isSecuredEndPoint(ServerHttpRequest request) {
        return OpenedAndSecuredPathsService.openEndPoints
            .stream()
            .noneMatch(url -> request.getURI().getPath().contains(url));
    }
}
