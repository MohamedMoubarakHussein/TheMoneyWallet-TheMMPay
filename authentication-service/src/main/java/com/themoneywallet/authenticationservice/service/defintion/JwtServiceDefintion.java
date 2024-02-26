package com.themoneywallet.authenticationservice.service.defintion;

public interface JwtServiceDefintion {
    String generateToken(String email);
    boolean isTokenValid(String token , String email);
}
