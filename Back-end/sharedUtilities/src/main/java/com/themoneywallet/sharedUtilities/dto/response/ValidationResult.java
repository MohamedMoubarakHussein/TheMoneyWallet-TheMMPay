package com.themoneywallet.sharedUtilities.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the result of a validation operation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    
    @Builder.Default
    private boolean valid = true;
    
    @Builder.Default
    private List<String> errorMessages = new ArrayList<>();
    
    @Builder.Default
    private Map<String, String> fieldErrors = Map.of();
    
    private String errorCode;
    
    /**
     * Creates a successful validation result
     */
    public static ValidationResult success() {
        return ValidationResult.builder().valid(true).build();
    }
    
    /**
     * Creates a failed validation result with a single error message
     */
    public static ValidationResult failure(String errorMessage) {
        return ValidationResult.builder()
                .valid(false)
                .errorMessages(List.of(errorMessage))
                .build();
    }
    
    /**
     * Creates a failed validation result with an error code and message
     */
    public static ValidationResult failure(String errorMessage, String errorCode) {
        return ValidationResult.builder()
                .valid(false)
                .errorMessages(List.of(errorMessage))
                .errorCode(errorCode)
                .build();
    }
    
    /**
     * Creates a failed validation result with field-specific errors
     */
    public static ValidationResult failure(Map<String, String> fieldErrors) {
        return ValidationResult.builder()
                .valid(false)
                .fieldErrors(fieldErrors)
                .build();
    }
    
    /**
     * Adds an error message to the validation result
     */
    public ValidationResult addError(String errorMessage) {
        this.valid = false;
        if (this.errorMessages == null) {
            this.errorMessages = new ArrayList<>();
        }
        this.errorMessages.add(errorMessage);
        return this;
    }
    
    /**
     * Combines this validation result with another
     */
    public ValidationResult combine(ValidationResult other) {
        if (other == null || other.isValid()) {
            return this;
        }
        
        this.valid = false;
        
        if (other.getErrorMessages() != null && !other.getErrorMessages().isEmpty()) {
            if (this.errorMessages == null) {
                this.errorMessages = new ArrayList<>();
            }
            this.errorMessages.addAll(other.getErrorMessages());
        }
        
        if (other.getFieldErrors() != null && !other.getFieldErrors().isEmpty()) {
            if (this.fieldErrors == null) {
                this.fieldErrors = Map.of();
            }
            this.fieldErrors.putAll(other.getFieldErrors());
        }
        
        return this;
    }
}

