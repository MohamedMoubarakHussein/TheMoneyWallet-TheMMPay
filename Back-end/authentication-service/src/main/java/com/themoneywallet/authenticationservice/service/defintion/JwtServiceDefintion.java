package com.themoneywallet.authenticationservice.service.defintion;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtServiceDefintion {
    String generateToken(String email);
    boolean isTokenValid(String token , UserDetails userDetails);
}
