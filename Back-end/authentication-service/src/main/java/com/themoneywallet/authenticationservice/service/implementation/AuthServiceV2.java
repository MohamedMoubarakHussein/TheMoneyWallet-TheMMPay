package com.themoneywallet.authenticationservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.response.UnifiedResponse;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.entity.fixed.FixedInternalValues;
import com.themoneywallet.authenticationservice.entity.fixed.ResponseKey;
import com.themoneywallet.authenticationservice.entity.fixed.UserRole;
import com.themoneywallet.authenticationservice.event.Event;
import com.themoneywallet.authenticationservice.event.EventType;
import com.themoneywallet.authenticationservice.event.UserCreationEvent;
import com.themoneywallet.authenticationservice.event.UserLogInEvent;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.utilities.DatabaseHelper;
import com.themoneywallet.authenticationservice.utilities.SerializationDeHalper;
import com.themoneywallet.authenticationservice.utilities.UnifidResponseHandler;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceV2 {

    private final UserCredentialRepository userCredentialRepository;

    public UserCredential createOrUpdateOAuth2User(
        String email,
        String name,
        String providerId
    ) {
        Optional<UserCredential> existingUser =
            this.userCredentialRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // Update existing user with OAuth info if needed
            UserCredential user = existingUser.get();
            user.setOauth2ProviderId(providerId);
            return this.userCredentialRepository.save(user);
        } else {
            // Create new user from OAuth data
            UserCredential newUser = new UserCredential();
            newUser.setEmail(email);
            newUser.setOauth2ProviderId(providerId);
            newUser.setEnabled(true); // OAuth users are pre-verified
            return userCredentialRepository.save(newUser);
        }
    }
}
