package com.themoneywallet.sharedUtilities.patterns.strategy;

import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Context class for validation strategies
 * Manages execution of multiple validation strategies
 */
@Component
@Slf4j
public class ValidationContext {
    
    /**
     * Executes a single validation strategy
     */
    public <T> ValidationResult validate(ValidationStrategy<T> strategy, T data) {
        log.debug("Executing validation strategy: {}", strategy.getStrategyName());
        
        try {
            ValidationResult result = strategy.validate(data);
            log.debug("Validation strategy {} completed with result: {}", 
                    strategy.getStrategyName(), result.isValid() ? "VALID" : "INVALID");
            return result;
        } catch (Exception e) {
            log.error("Error executing validation strategy {}: {}", 
                    strategy.getStrategyName(), e.getMessage(), e);
            return ValidationResult.failure("Validation error: " + e.getMessage());
        }
    }
    
    /**
     * Executes multiple validation strategies in order
     * Stops at first failure if stopOnFailure is true
     */
    public <T> ValidationResult validate(List<ValidationStrategy<T>> strategies, T data, boolean stopOnFailure) {
        if (strategies == null || strategies.isEmpty()) {
            return ValidationResult.success();
        }
        
        // Sort strategies by order
        List<ValidationStrategy<T>> sortedStrategies = strategies.stream()
                .sorted((s1, s2) -> Integer.compare(s1.getOrder(), s2.getOrder()))
                .collect(Collectors.toList());
        
        ValidationResult combinedResult = ValidationResult.success();
        
        for (ValidationStrategy<T> strategy : sortedStrategies) {
            ValidationResult result = validate(strategy, data);
            
            if (!result.isValid()) {
                combinedResult.combine(result);
                if (stopOnFailure) {
                    log.debug("Stopping validation chain due to failure in strategy: {}", 
                            strategy.getStrategyName());
                    break;
                }
            }
        }
        
        return combinedResult;
    }
    
    /**
     * Executes multiple validation strategies and collects all errors
     */
    public <T> ValidationResult validateAll(List<ValidationStrategy<T>> strategies, T data) {
        return validate(strategies, data, false);
    }
    
    /**
     * Executes validation strategies until first failure
     */
    public <T> ValidationResult validateUntilFailure(List<ValidationStrategy<T>> strategies, T data) {
        return validate(strategies, data, true);
    }
}

