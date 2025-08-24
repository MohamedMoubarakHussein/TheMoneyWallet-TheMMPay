package com.themoneywallet.transactionservice.service.interfaces;

import com.themoneywallet.transactionservice.dto.request.TransactionRequest;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;

/**
 * Interface for transaction validation operations
 * Follows Interface Segregation Principle
 */
public interface TransactionValidationService {
    
    /**
     * Validates transaction request data
     */
    ValidationResult validateTransactionRequest(TransactionRequest request);
    
    /**
     * Validates transaction business rules
     */
    ValidationResult validateBusinessRules(TransactionRequest request);
}

