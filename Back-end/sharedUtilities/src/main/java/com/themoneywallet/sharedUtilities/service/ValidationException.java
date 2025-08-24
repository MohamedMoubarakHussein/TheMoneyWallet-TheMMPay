package com.themoneywallet.sharedUtilities.service;

import lombok.Getter;

/**
 * Exception for validation errors
 */
@Getter
public class ValidationException extends RuntimeException {
    
    private final String errorCode;
    
    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ValidationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}

