package com.themoneywallet.sharedUtilities.patterns.strategy;

import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;

/**
 * Strategy interface for validation logic
 * Follows Strategy Pattern to allow different validation implementations
 */
public interface ValidationStrategy<T> {
    
    /**
     * Validates the input data
     * @param data the data to validate
     * @return validation result containing success status and any error messages
     */
    ValidationResult validate(T data);
    
    /**
     * Returns the strategy name for identification
     * @return strategy name
     */
    String getStrategyName();
    
    /**
     * Returns the order of execution when multiple strategies are chained
     * Lower values execute first
     * @return execution order
     */
    default int getOrder() {
        return 0;
    }
}

