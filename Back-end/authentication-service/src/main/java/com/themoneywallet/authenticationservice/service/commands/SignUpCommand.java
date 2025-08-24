package com.themoneywallet.authenticationservice.service.commands;

import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.service.interfaces.TokenManagementService;
import com.themoneywallet.authenticationservice.service.interfaces.UserValidationService;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.domain.events.UserSignedUpEvent;
import com.themoneywallet.sharedUtilities.patterns.command.Command;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import com.themoneywallet.sharedUtilities.enums.UserRole;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.themoneywallet.sharedUtilities.service.ValidationException;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Command for user sign up operation
 * Follows Command Pattern and Single Responsibility Principle
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SignUpCommand implements Command<SignUpRequest, ResponseEntity<UnifiedResponse>> {
    
    private final UserCredentialRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenManagementService tokenManagementService;
    private final UserValidationService userValidationService;
    private final UnifidResponseHandler responseHandler;
    private final EventPublisher eventPublisher;
    
    private static final Integer EMAIL_TOKEN_VALID_TILL_M = 3;
    private static final Integer COOKIE_MAX_AGE_H = 7;
    
    @Override
    public ResponseEntity<UnifiedResponse> execute(SignUpRequest request) {
        log.info("Executing sign up command for user: {}", request.getUserName());
        
        // Validate input using validation service
        ValidationResult validationResult = userValidationService.validateSignUpData(request);
        if (!validationResult.isValid()) {
            throw new ValidationException("Validation failed", "AUVD1003");
        }
        
        // Create user credential
        UserCredential credential = createUserCredential(request);
        
        // Save to database
        credential = userRepository.save(credential);
        
        // Publish domain event
        publishSignUpEvent(request, credential);
        
        // Create response headers with tokens
        var headers = tokenManagementService.createAuthHeaders(
                credential.getToken(), credential.getAccessToken());
        
        return responseHandler.generateSuccessResponse("data", credential, 
                HttpStatus.CREATED, headers);
    }
    
    @Override
    public boolean canExecute(SignUpRequest input) {
        return input != null && 
               input.getUserName() != null && 
               input.getEmail() != null && 
               input.getPassword() != null;
    }
    
    @Override
    public String getCommandName() {
        return "SignUpCommand";
    }
    
    private UserCredential createUserCredential(SignUpRequest request) {
        UUID userId = UuidCreator.getTimeOrderedEpoch();
        Map<String, String> tokens = tokenManagementService.generateTokens(
                request.getUserName(), userId, UserRole.ROLE_USER.toString());
        
        return UserCredential.builder()
                .userId(userId)
                .email(request.getEmail())
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRole(UserRole.ROLE_USER)
                .locked(false)
                .enabled(false)
                .lastLogin(LocalDateTime.now())
                .emailTokenValidTill(LocalDateTime.now().plusMinutes(EMAIL_TOKEN_VALID_TILL_M))
                .tokenValidTill(LocalDateTime.now().plusHours(COOKIE_MAX_AGE_H))
                .revoked(false)
                .emailVerificationToken(tokens.get("signUpToken"))
                .token(tokens.get("refreshToken"))
                .accessToken(tokens.get("accessToken"))
                .build();
    }
    
    private void publishSignUpEvent(SignUpRequest request, UserCredential credential) {
        UserSignedUpEvent event = new UserSignedUpEvent(
                credential.getUserId().toString(),
                credential.getEmail(),
                credential.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                credential.getUserRole().toString(),
                credential.getEmailVerificationToken()
        );
        
        eventPublisher.publish(event);
    }
}

