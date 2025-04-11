package com.themoneywallet.authenticationservice.service.defintion;

import org.springframework.http.ResponseEntity;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.response.UnifiedResponse;

public interface AuthServiceDefintion {
    ResponseEntity<UnifiedResponse> signUp(SignUpRequest request);
    ResponseEntity<UnifiedResponse> signIn(AuthRequest request);
    ResponseEntity<UnifiedResponse> validToken(String Token);
}
