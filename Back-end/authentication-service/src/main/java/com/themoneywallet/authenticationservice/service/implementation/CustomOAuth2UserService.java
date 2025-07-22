package com.themoneywallet.authenticationservice.service.implementation;

import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.sharedUtilities.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserCredentialRepository userCredentialRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        // Extract user information from OAuth2 response
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub"); // Google's user ID
        String provider = userRequest.getClientRegistration().getRegistrationId();
        
        log.info("OAuth2 user login attempt: email={}, provider={}", email, provider);
        
        // Check if user already exists
        Optional<UserCredential> existingUser = userCredentialRepository.findByEmail(email);
        
        UserCredential userCredential;
        if (existingUser.isPresent()) {
            userCredential = existingUser.get();
            // Update OAuth2 information if it's a new OAuth2 login for existing user
            if (!userCredential.isOAuth2User()) {
                userCredential.setOAuth2User(true);
                userCredential.setOauth2Provider(provider);
                userCredential.setOauth2ProviderId(providerId);
            }
            userCredential.setLastLogin(LocalDateTime.now());
        } else {
            // Create new OAuth2 user
            userCredential = UserCredential.builder()
                .email(email)
                .userName(name)
                .userRole(UserRole.ROLE_USER) 
                .isOAuth2User(true)
                .oauth2Provider(provider)
                .oauth2ProviderId(providerId)
                .enabled(true)
                .locked(false)
                .lastLogin(LocalDateTime.now())
                .build();
        }
        
        userCredentialRepository.save(userCredential);
        
        // Return custom OAuth2User implementation
        return new CustomOAuth2User(oAuth2User, userCredential);
    }
}