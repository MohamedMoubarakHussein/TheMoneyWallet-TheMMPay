package com.themoneywallet.sharedUtilities.handler;

import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.service.BusinessException;
import com.themoneywallet.sharedUtilities.service.ValidationException;
import com.themoneywallet.sharedUtilities.utilities.UnifiedResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Standardized exception handler for all services
 * Follows DRY principle and provides consistent error responses
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class StandardizedExceptionHandler {
    
    private final UnifiedResponseHandler responseHandler;
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    /**
     * Handles custom validation exceptions
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<UnifiedResponse> handleValidationException(ValidationException ex, WebRequest request) {
        log.warn("Validation error: {} (Code: {})", ex.getMessage(), ex.getErrorCode());
        
        return responseHandler.generateFailedResponse(
                "validationError", 
                ex.getMessage(),
                ex.getErrorCode(), 
                "String", 
                HttpStatus.BAD_REQUEST
        );
    }
    
    /**
     * Handles custom business exceptions
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<UnifiedResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        log.warn("Business error: {} (Code: {})", ex.getMessage(), ex.getErrorCode());
        
        return responseHandler.generateFailedResponse(
                "businessError", 
                ex.getMessage(),
                ex.getErrorCode(), 
                "String", 
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
    
    /**
     * Handles bean validation errors for @Valid annotated request bodies
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UnifiedResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField, 
                        FieldError::getDefaultMessage, 
                        (msg1, msg2) -> msg1
                ));
        
        log.warn("Field validation errors: {}", fieldErrors);
        
        return responseHandler.generateFailedResponse(
                "fieldValidationError", 
                fieldErrors,
                "VAL001", 
                "Map<String,String>", 
                HttpStatus.BAD_REQUEST
        );
    }
    
    /**
     * Handles illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<UnifiedResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        return responseHandler.generateFailedResponse(
                "invalidArgument", 
                ex.getMessage(),
                "ARG001", 
                "String", 
                HttpStatus.BAD_REQUEST
        );
    }
    
    /**
     * Handles illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<UnifiedResponse> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        
        log.warn("Illegal state: {}", ex.getMessage());
        
        return responseHandler.generateFailedResponse(
                "invalidState", 
                ex.getMessage(),
                "STATE001", 
                "String", 
                HttpStatus.CONFLICT
        );
    }
    
    /**
     * Catch-all handler for unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<UnifiedResponse> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        String message;
        String errorCode = "INT001";
        
        if ("dev".equals(activeProfile)) {
            message = "An error occurred: " + ex.getMessage();
        } else {
            message = "An unexpected error has occurred. Please contact our customer service team " +
                     "and provide the following error code: #" + errorCode + ". We apologize for the inconvenience.";
        }
        
        return responseHandler.generateFailedResponse(
                "internalError", 
                message,
                errorCode, 
                "String", 
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}

