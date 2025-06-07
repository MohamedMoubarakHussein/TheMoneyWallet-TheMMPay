package com.themoneywallet.authenticationservice.service.defintion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.response.UnifiedResponse;
import jakarta.servlet.ServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthServiceDefintion {
    ResponseEntity<UnifiedResponse> signUp(
        SignUpRequest request,
        ServletRequest userIdent
    );
    ResponseEntity<UnifiedResponse> signIn(
        AuthRequest request,
        ServletRequest req
    ) throws JsonProcessingException;
    boolean validToken(String Token);
}
