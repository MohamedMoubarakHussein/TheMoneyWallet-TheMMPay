package com.themoneywallet.authenticationservice.service.implementation;

import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.service.interfaces.UserValidationService;
import com.themoneywallet.authenticationservice.service.validation.SignUpValidationStrategy;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.sharedUtilities.patterns.strategy.ValidationContext;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserValidationService
 * Follows Single Responsibility Principle - only handles validation operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationServiceImpl implements UserValidationService {
    
    private final ValidationContext validationContext;
    private final SignUpValidationStrategy signUpValidationStrategy;
    private final UserCredentialRepository userRepository;
    private final JwtService jwtService;
    
    @Override
    public ValidationResult validateSignUpData(SignUpRequest request) {
        log.debug("Validating sign up data for user: {}", request.getUserName());
        
        return validationContext.validateAll(
                List.of(signUpValidationStrategy), 
                request
        );
    }
    
    @Override
    public ValidationResult validateEmailToken(String token, String verificationCode) {
        log.debug("Validating email verification token");
        
        try {
            UUID userId = jwtService.extractUserId(token);
            Optional<UserCredential> opUser = userRepository.findByUserId(userId);
            
            if (opUser.isEmpty()) {
                return ValidationResult.failure("User not found", "AUCR1009");
            }
            
            UserCredential user = opUser.get();
            
            if (!user.getEmailVerificationToken().equals(verificationCode)) {
                return ValidationResult.failure("Invalid verification code", "AUCR1009");
            }
            
            if (user.getEmailTokenValidTill().isBefore(LocalDateTime.now())) {
                return ValidationResult.failure("Verification code has expired", "AUCR1009");
            }
            
            return ValidationResult.success();
            
        } catch (Exception e) {
            log.error("Error validating email token: {}", e.getMessage());
            return ValidationResult.failure("Invalid token", "AUCR1009");
        }
    }
    
    @Override
    public ValidationResult validatePasswordResetToken(String token, String resetCode) {
        log.debug("Validating password reset token");
        
        try {
            Optional<UserCredential> opUser = userRepository.findByToken(token);
            
            if (opUser.isEmpty()) {
                return ValidationResult.failure("User not found", "AUTK0012");
            }
            
            UserCredential user = opUser.get();
            
            if (!resetCode.equals(user.getPasswordResetToken())) {
                return ValidationResult.failure("Invalid reset code", "AUTK0012");
            }
            
            return ValidationResult.success();
            
        } catch (Exception e) {
            log.error("Error validating password reset token: {}", e.getMessage());
            return ValidationResult.failure("Invalid token", "AUTK0012");
        }
    }
}

