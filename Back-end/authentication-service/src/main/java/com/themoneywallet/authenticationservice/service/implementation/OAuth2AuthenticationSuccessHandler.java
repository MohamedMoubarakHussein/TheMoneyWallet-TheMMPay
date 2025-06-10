package com.themoneywallet.authenticationservice.service.implementation;

import com.themoneywallet.authenticationservice.entity.UserCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
    implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Lazy
    private final AuthServiceV2 authService;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        // Extract user info from OAuth2 authentication
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        // Create or update user in your database
        UserCredential user = authService.createOrUpdateOAuth2User(
            email,
            name,
            authentication.getName()
        );

        // Generate your JWT token
        String jwtToken = this.jwtService.generateToken(user.getEmail());

        // You can redirect to frontend with token or set it in cookie
        String redirectUrl =
            "http://localhost:3000/oauth2/redirect?token=" + jwtToken;
        response.sendRedirect(redirectUrl);
    }
}
