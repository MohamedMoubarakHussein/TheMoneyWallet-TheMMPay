package com.themoneywallet.transactionservice.service.interfaces;

/**
 * Interface for transaction processing operations
 * Follows Interface Segregation Principle
 */
public interface TransactionProcessingService {
    
    /**
     * Processes wallet debit response
     */
    void processWalletDebitResponse(String transactionId, boolean success);
    
    /**
     * Processes wallet credit response
     */
    void processWalletCreditResponse(String transactionId, boolean success);
    
    /**
     * Processes user validation response
     */
    void processUserValidationResponse(String transactionId, boolean isValid);
}

