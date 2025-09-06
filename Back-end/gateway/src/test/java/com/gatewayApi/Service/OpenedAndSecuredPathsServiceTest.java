package com.gatewayApi.Service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import com.gatewayapi.Service.OpenedAndSecuredPathsService;

public class OpenedAndSecuredPathsServiceTest {

    private OpenedAndSecuredPathsService openedAndSecuredPathsService;

    @BeforeEach
    void setup() {
        this.openedAndSecuredPathsService = new OpenedAndSecuredPathsService();
    }

    @Test
    void requestToOpenEndPoint() {
        MockServerHttpRequest req = MockServerHttpRequest.get(
            "/auth/signup"
        ).build();

        assertFalse(
            this.openedAndSecuredPathsService.isSecuredEndPoint(req),
            "Excpected open end point to return false"
        );
    }

    @Test
    void requestToSecureEndPoint() {
        MockServerHttpRequest req = MockServerHttpRequest.get(
            "/cs/signupxd/c"
        ).build();

        assertTrue(
            this.openedAndSecuredPathsService.isSecuredEndPoint(req),
            "Expected secured endpoint to return true"
        );
    }
}
