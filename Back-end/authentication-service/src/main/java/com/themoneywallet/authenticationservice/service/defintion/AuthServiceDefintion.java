package com.themoneywallet.authenticationservice.service.defintion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import jakarta.servlet.ServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthServiceDefintion {
    ResponseEntity<String> signUp(
        SignUpRequest request,
        ServletRequest userIdent
    ) throws JsonProcessingException;
    ResponseEntity<String> signIn(AuthRequest request, ServletRequest req)
        throws JsonProcessingException;
    boolean validToken(String Token);
}
