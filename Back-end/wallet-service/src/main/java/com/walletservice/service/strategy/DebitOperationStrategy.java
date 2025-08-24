package com.walletservice.service.strategy;

import com.walletservice.entity.Wallet;
import com.walletservice.dto.request.UpdateBalanceRequestDTO;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy for debit operations
 * Follows Strategy Pattern
 */
@Component
@Slf4j
public class DebitOperationStrategy implements BalanceOperationStrategy {
    
    @Override
    public void performOperation(Wallet wallet, UpdateBalanceRequestDTO request) {
        log.debug("Performing debit operation for wallet: {}, amount: {}", 
                wallet.getWalletId(), request.getAmount());
        
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(request.getAmount()));
    }
    
    @Override
    public ValidationResult validateOperation(Wallet wallet, UpdateBalanceRequestDTO request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.failure("Debit amount must be positive", "INVALID_AMOUNT");
        }
        
        // Check if sufficient balance available
        if (wallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            return ValidationResult.failure("Insufficient available balance", "INSUFFICIENT_FUNDS");
        }
        
        // Check transaction limits
        if (request.getAmount().compareTo(wallet.getLimits().getMaxTransactionAmount()) > 0) {
            return ValidationResult.failure("Amount exceeds maximum transaction limit", "LIMIT_EXCEEDED");
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public String getOperationType() {
        return "DEBIT";
    }
}

