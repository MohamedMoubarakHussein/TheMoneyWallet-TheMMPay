package com.themoneywallet.authenticationservice.service.interfaces;

import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.UUID;

/**
 * Interface for token management operations
 * Follows Interface Segregation Principle
 */
public interface TokenManagementService {
    
    /**
     * Generates all required tokens for a user
     */
    Map<String, String> generateTokens(String userName, UUID userId, String userRole);
    
    /**
     * Creates authentication headers with tokens
     */
    HttpHeaders createAuthHeaders(String refreshToken, String accessToken);
    
    /**
     * Validates if a token is valid
     */
    boolean isTokenValid(String token);
    
    /**
     * Revokes a refresh token
     */
    void revokeToken(String token);
    
    /**
     * Refreshes tokens using a refresh token
     */
    Map<String, String> refreshTokens(String refreshToken);
}

