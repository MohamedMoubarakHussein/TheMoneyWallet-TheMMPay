package com.themoneywallet.authenticationservice.service.validation;

import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.sharedUtilities.patterns.strategy.ValidationStrategy;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Validation strategy for sign up requests
 * Follows Strategy Pattern for validation logic
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SignUpValidationStrategy implements ValidationStrategy<SignUpRequest> {
    
    private final UserCredentialRepository userRepository;
    
    @Override
    public ValidationResult validate(SignUpRequest data) {
        log.debug("Validating sign up data for user: {}", data.getUserName());
        
        Map<String, String> fieldErrors = new HashMap<>();
        
        // Check if email already exists
        if (userRepository.findByEmail(data.getEmail()).isPresent()) {
            fieldErrors.put("email", "This email address is already in use.");
        }
        
        // Check if username already exists
        if (userRepository.findByUserName(data.getUserName()).isPresent()) {
            fieldErrors.put("userName", "This username is already in use.");
        }
        
        // Validate email format
        if (!isValidEmail(data.getEmail())) {
            fieldErrors.put("email", "Invalid email format.");
        }
        
        // Validate password strength
        if (!isValidPassword(data.getPassword())) {
            fieldErrors.put("password", "Password must be at least 8 characters long and contain letters and numbers.");
        }
        
        if (fieldErrors.isEmpty()) {
            return ValidationResult.success();
        } else {
            return ValidationResult.failure(fieldErrors);
        }
    }
    
    @Override
    public String getStrategyName() {
        return "SignUpValidationStrategy";
    }
    
    @Override
    public int getOrder() {
        return 1;
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
    
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        
        return hasLetter && hasDigit;
    }
}

