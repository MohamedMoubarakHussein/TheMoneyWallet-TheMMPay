package com.walletservice.service.strategy;

import com.walletservice.entity.Wallet;
import com.walletservice.dto.request.UpdateBalanceRequestDTO;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy for reserve operations
 * Follows Strategy Pattern
 */
@Component
@Slf4j
public class ReserveOperationStrategy implements BalanceOperationStrategy {
    
    @Override
    public void performOperation(Wallet wallet, UpdateBalanceRequestDTO request) {
        log.debug("Performing reserve operation for wallet: {}, amount: {}", 
                wallet.getWalletId(), request.getAmount());
        
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(request.getAmount()));
        wallet.setReservedBalance(wallet.getReservedBalance().add(request.getAmount()));
    }
    
    @Override
    public ValidationResult validateOperation(Wallet wallet, UpdateBalanceRequestDTO request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.failure("Reserve amount must be positive", "INVALID_AMOUNT");
        }
        
        // Check if sufficient balance available to reserve
        if (wallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            return ValidationResult.failure("Insufficient available balance to reserve", "INSUFFICIENT_FUNDS");
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public String getOperationType() {
        return "RESERVE";
    }
}

