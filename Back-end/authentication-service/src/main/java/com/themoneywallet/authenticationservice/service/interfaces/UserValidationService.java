package com.themoneywallet.authenticationservice.service.interfaces;

import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;

/**
 * Interface for user validation operations
 * Follows Interface Segregation Principle
 */
public interface UserValidationService {
    
    /**
     * Validates user data before sign up
     */
    ValidationResult validateSignUpData(SignUpRequest request);
    
    /**
     * Validates email verification token
     */
    ValidationResult validateEmailToken(String token, String verificationCode);
    
    /**
     * Validates password reset token
     */
    ValidationResult validatePasswordResetToken(String token, String resetCode);
}

