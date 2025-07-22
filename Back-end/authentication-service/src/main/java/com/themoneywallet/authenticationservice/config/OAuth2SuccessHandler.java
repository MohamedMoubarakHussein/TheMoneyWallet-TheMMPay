package com.themoneywallet.authenticationservice.config;

import com.themoneywallet.authenticationservice.service.implementation.CustomOAuth2User;
import com.themoneywallet.authenticationservice.service.implementation.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        
        // Generate JWT token for the OAuth2 user
        String jwtToken = jwtService.generateToken(oAuth2User.getUserCredential().getUsername() ,oAuth2User.getUserCredential().getUserId() ,  oAuth2User.getUserCredential().getUserRole().name());
        
        // Redirect to frontend with JWT token
        String redirectUrl = buildRedirectUrl(jwtToken);
        
        log.info("OAuth2 authentication successful for user: {}", oAuth2User.getName());
        
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String buildRedirectUrl(String jwtToken) {
        // Change this URL to your frontend URL
        String frontendUrl = "http://localhost:3000/auth/oauth2/callback";
        
        try {
            return frontendUrl + "?token=" + URLEncoder.encode(jwtToken, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error encoding JWT token for redirect", e);
            return frontendUrl + "?error=token_encoding_failed";
        }
    }
} 