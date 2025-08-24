package com.themoneywallet.sharedUtilities.service;

import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.patterns.observer.DomainEvent;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.patterns.strategy.ValidationContext;
import com.themoneywallet.sharedUtilities.patterns.strategy.ValidationStrategy;
import com.themoneywallet.sharedUtilities.utilities.UnifiedResponseHandler;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base service class following Template Method Pattern
 * Provides common functionality for all services
 */
@RequiredArgsConstructor
@Slf4j
public abstract class BaseService<I> {
    
    protected final UnifiedResponseHandler responseHandler;
    protected final EventPublisher eventPublisher;
    protected final ValidationContext validationContext;
    
    /**
     * Template method for handling service operations
     * Follows Template Method Pattern
     */
    protected <R> ResponseEntity<UnifiedResponse> executeOperation(
            String operationName, 
            I input, 
            ServiceOperation<I, R> operation) {
        
        log.info("Starting operation: {}", operationName);
        
        try {
            // Pre-operation validation
            validateInput(input, getValidationStrategies());
            
            // Execute the actual operation
            R result = operation.execute(input);
            
            // Post-operation processing
            postProcessResult(result);
            
            log.info("Operation {} completed successfully", operationName);
            return responseHandler.generateSuccessResponse("data", result, HttpStatus.OK);
            
        } catch (ValidationException e) {
            log.warn("Validation failed for operation {}: {}", operationName, e.getMessage());
            return responseHandler.generateFailedResponse("validationError", e.getMessage(), 
                    e.getErrorCode(), "String", HttpStatus.BAD_REQUEST);
                    
        } catch (BusinessException e) {
            log.error("Business error in operation {}: {}", operationName, e.getMessage());
            return responseHandler.generateFailedResponse("businessError", e.getMessage(), 
                    e.getErrorCode(), "String", HttpStatus.UNPROCESSABLE_ENTITY);
                    
        } catch (Exception e) {
            log.error("Unexpected error in operation {}: {}", operationName, e.getMessage(), e);
            return responseHandler.generateFailedResponse("internalError", 
                    "An unexpected error occurred", "INT001", "String", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Validates input data using provided strategies.
     * Subclasses can provide specific validation strategies.
     */
    protected void validateInput(I input, List<ValidationStrategy<I>> strategies) {
        if (strategies == null || strategies.isEmpty()) {
            // If no specific strategies are provided, perform a basic null check.
            if (input == null) {
                throw new ValidationException("Input cannot be null", "VAL001");
            }
            return;
        }

        ValidationResult result = validationContext.validateAll(strategies, input);
        if (!result.isValid()) {
            // Combine all error messages from the ValidationResult
            String errorMessage = result.getErrorMessages().stream()
                                        .collect(Collectors.joining("; "));
            if (!result.getFieldErrors().isEmpty()) {
                String fieldErrorMessages = result.getFieldErrors().entrySet().stream()
                                                .map(entry -> entry.getKey() + ": " + entry.getValue())
                                                .collect(Collectors.joining("; "));
                if (!errorMessage.isEmpty()) {
                    errorMessage += "; ";
                }
                errorMessage += fieldErrorMessages;
            }
            throw new ValidationException(errorMessage, "VAL002"); // Use a generic validation error code
        }
    }
    
    /**
     * Post-processes the result - to be implemented by subclasses
     */
    protected <R> void postProcessResult(R result) {
        // Default implementation - can be overridden
    }
    
    /**
     * Publishes a domain event
     */
    protected void publishEvent(DomainEvent event) {
        try {
            eventPublisher.publish(event);
        } catch (Exception e) {
            log.error("Failed to publish event {}: {}", event.getEventType(), e.getMessage(), e);
        }
    }
    
    /**
     * Functional interface for service operations
     */
    @FunctionalInterface
    protected interface ServiceOperation<I, R> {
        R execute(I input) throws Exception;
    }

    /**
     * Subclasses must implement this to provide specific validation strategies.
     */
    protected abstract List<ValidationStrategy<I>> getValidationStrategies();
}

