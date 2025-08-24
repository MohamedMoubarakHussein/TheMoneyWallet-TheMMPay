package com.walletservice.service.strategy;

import com.walletservice.entity.Wallet;
import com.walletservice.dto.request.UpdateBalanceRequestDTO;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy for release operations
 * Follows Strategy Pattern
 */
@Component
@Slf4j
public class ReleaseOperationStrategy implements BalanceOperationStrategy {
    
    @Override
    public void performOperation(Wallet wallet, UpdateBalanceRequestDTO request) {
        log.debug("Performing release operation for wallet: {}, amount: {}", 
                wallet.getWalletId(), request.getAmount());
        
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.getAmount()));
        wallet.setReservedBalance(wallet.getReservedBalance().subtract(request.getAmount()));
    }
    
    @Override
    public ValidationResult validateOperation(Wallet wallet, UpdateBalanceRequestDTO request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.failure("Release amount must be positive", "INVALID_AMOUNT");
        }
        
        // Check if sufficient reserved balance to release
        if (wallet.getReservedBalance().compareTo(request.getAmount()) < 0) {
            return ValidationResult.failure("Insufficient reserved balance to release", "INSUFFICIENT_RESERVED_FUNDS");
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public String getOperationType() {
        return "RELEASE";
    }
}

