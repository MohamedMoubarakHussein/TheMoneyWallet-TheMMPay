package com.themoneywallet.authenticationservice.service.defintion;

import org.springframework.http.ResponseEntity;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;

public interface AuthServiceDefintion {
    ResponseEntity<String> signUp(SignUpRequest request);
    ResponseEntity<String> signIn(AuthRequest request);
    boolean validToken(String Token  );
}
