package com.themoneywallet.authenticationservice.service.defintion;

import java.util.UUID;

public interface JwtServiceDefintion {
    String generateToken(String username , UUID  userId, String role);
}
