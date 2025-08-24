package com.walletservice.service.strategy;

import com.walletservice.entity.Wallet;
import com.walletservice.dto.request.UpdateBalanceRequestDTO;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;

/**
 * Strategy interface for different balance operations
 * Follows Strategy Pattern for balance operation logic
 */
public interface BalanceOperationStrategy {
    
    /**
     * Performs the balance operation on the wallet
     */
    void performOperation(Wallet wallet, UpdateBalanceRequestDTO request);
    
    /**
     * Validates if the operation can be performed
     */
    ValidationResult validateOperation(Wallet wallet, UpdateBalanceRequestDTO request);
    
    /**
     * Returns the operation type this strategy handles
     */
    String getOperationType();
}
