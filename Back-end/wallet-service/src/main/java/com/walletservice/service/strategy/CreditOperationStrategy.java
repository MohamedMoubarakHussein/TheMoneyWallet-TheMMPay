package com.walletservice.service.strategy;

import com.walletservice.entity.Wallet;
import com.walletservice.dto.request.UpdateBalanceRequestDTO;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy for credit operations
 * Follows Strategy Pattern
 */
@Component
@Slf4j
public class CreditOperationStrategy implements BalanceOperationStrategy {
    
    @Override
    public void performOperation(Wallet wallet, UpdateBalanceRequestDTO request) {
        log.debug("Performing credit operation for wallet: {}, amount: {}", 
                wallet.getWalletId(), request.getAmount());
        
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.getAmount()));
    }
    
    @Override
    public ValidationResult validateOperation(Wallet wallet, UpdateBalanceRequestDTO request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.failure("Credit amount must be positive", "INVALID_AMOUNT");
        }
        
        // Check if credit would exceed maximum balance
        BigDecimal newBalance = wallet.getBalance().add(request.getAmount());
        if (newBalance.compareTo(wallet.getLimits().getMaxBalance()) > 0) {
            return ValidationResult.failure("Credit would exceed maximum balance limit", "LIMIT_EXCEEDED");
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public String getOperationType() {
        return "CREDIT";
    }
}

