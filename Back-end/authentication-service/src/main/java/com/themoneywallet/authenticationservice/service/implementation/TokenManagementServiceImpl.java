package com.themoneywallet.authenticationservice.service.implementation;

import com.themoneywallet.authenticationservice.service.interfaces.TokenManagementService;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of TokenManagementService
 * Follows Single Responsibility Principle - only handles token operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenManagementServiceImpl implements TokenManagementService {
    
    private final JwtService jwtService;
    private final JwtRefService jwtRefService;
    private final UserCredentialRepository userRepository;
    
    private static final Integer COOKIE_MAX_AGE_H = 7;
    private static final String COOKIE_PATH = "/";
    private static final boolean IS_COOKIE_SECURE = false;
    private static final String COOKIE_SAME_SITE = "Lax";
    private static final boolean IS_COOKIE_HTTP_ONLY = true;
    
    @Override
    public Map<String, String> generateTokens(String userName, UUID userId, String userRole) {
        log.debug("Generating tokens for user: {}", userName);
        
        String accessToken = jwtService.generateToken(userName, userId, userRole);
        String refreshToken = jwtRefService.generateToken(userName, userId);
        String signUpToken = new BigInteger(30, new SecureRandom()).toString(32).toUpperCase();
        String forgetPasswordToken = new BigInteger(30, new SecureRandom()).toString(32).toUpperCase();
        
        String cookie = ResponseCookie.from("refToken", refreshToken)
                .httpOnly(IS_COOKIE_HTTP_ONLY)
                .secure(IS_COOKIE_SECURE)
                .sameSite(COOKIE_SAME_SITE)
                .maxAge(Duration.ofHours(COOKIE_MAX_AGE_H))
                .path(COOKIE_PATH)
                .build()
                .toString();
        
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "signUpToken", signUpToken,
                "forgetPassword", forgetPasswordToken,
                "cookie", cookie
        );
    }
    
    @Override
    public HttpHeaders createAuthHeaders(String refreshToken, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshToken);
        headers.add("Authorization", accessToken);
        return headers;
    }
    
    @Override
    public boolean isTokenValid(String token) {
        return jwtService.isTokenValid(token);
    }
    
    @Override
    public void revokeToken(String token) {
        log.debug("Revoking token");
        
        Optional<UserCredential> opUser = userRepository.findByToken(token);
        if (opUser.isPresent()) {
            UserCredential userCredential = opUser.get();
            userCredential.setToken("");
            userCredential.setTokenValidTill(LocalDateTime.now());
            userCredential.setRevoked(true);
            userRepository.save(userCredential);
            
            log.debug("Token revoked for user: {}", userCredential.getUsername());
        }
    }
    
    @Override
    public Map<String, String> refreshTokens(String refreshToken) {
        log.debug("Refreshing tokens");
        
        Optional<UserCredential> opUser = userRepository.findByToken(refreshToken);
        if (opUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        UserCredential user = opUser.get();
        if (user.isRevoked()) {
            throw new IllegalArgumentException("Token has been revoked");
        }
        
        return generateTokens(user.getUsername(), user.getUserId(), user.getUserRole().toString());
    }
}

