package com.themoneywallet.transactionservice.service.interfaces;

import com.themoneywallet.transactionservice.dto.response.TransactionResponse;
import com.themoneywallet.transactionservice.dto.status.TransactionStatusResponse;
import com.themoneywallet.transactionservice.entity.Transaction;

import java.util.Optional;

/**
 * Interface for transaction query operations
 * Follows Interface Segregation Principle
 */
public interface TransactionQueryService {
    
    /**
     * Retrieves transaction by ID
     */
    Optional<Transaction> getTransactionById(String transactionId);
    
    /**
     * Gets transaction status by ID
     */
    TransactionStatusResponse getTransactionStatus(String transactionId);
    
    /**
     * Converts transaction entity to response DTO
     */
    TransactionResponse toTransactionResponse(Transaction transaction);
}

