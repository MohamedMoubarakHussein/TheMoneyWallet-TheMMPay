package com.themoneywallet.authenticationservice.service.defintion;


public interface JwtServiceDefintion {
    String generateToken(String email , String  userId);
}
