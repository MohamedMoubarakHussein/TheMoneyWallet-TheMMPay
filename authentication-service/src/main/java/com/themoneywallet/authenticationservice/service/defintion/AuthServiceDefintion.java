package com.themoneywallet.authenticationservice.service.defintion;

import org.springframework.http.ResponseEntity;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.response.JwtToken;

public interface AuthServiceDefintion {
    ResponseEntity<JwtToken> signUp(SignUpRequest request);
    ResponseEntity<JwtToken> signIn(AuthRequest request);
    ResponseEntity<Boolean> validToken(String Token);
}
