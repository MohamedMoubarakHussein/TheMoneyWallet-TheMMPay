package com.themoneywallet.transactionservice.service.validation;

import com.themoneywallet.transactionservice.dto.request.TransactionRequest;
import com.themoneywallet.sharedUtilities.patterns.strategy.ValidationStrategy;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Validation strategy for transaction requests
 * Follows Strategy Pattern for validation logic
 */
@Component
@Slf4j
public class TransactionRequestValidationStrategy implements ValidationStrategy<TransactionRequest> {
    
    @Override
    public ValidationResult validate(TransactionRequest data) {
        log.debug("Validating transaction request for sender: {}", data.getSenderUserId());
        
        List<String> errors = new ArrayList<>();
        
        // Validate amount
        if (data.getAmount() == null || data.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Transaction amount must be positive");
        }
        
        // Validate sender user ID
        if (data.getSenderUserId() == null || data.getSenderUserId().trim().isEmpty()) {
            errors.add("Sender user ID is required");
        }
        
        // Validate transaction type
        if (data.getTransactionType() == null) {
            errors.add("Transaction type is required");
        }
        
        // Validate currency
        if (data.getCurrency() == null) {
            errors.add("Currency is required");
        }
        
        // For transfer transactions, receiver is required
        if (data.getTransactionType() != null && 
            data.getTransactionType().toString().equals("TRANSFER") &&
            (data.getReceiverUserId() == null || data.getReceiverUserId().trim().isEmpty())) {
            errors.add("Receiver user ID is required for transfer transactions");
        }
        
        if (errors.isEmpty()) {
            return ValidationResult.success();
        } else {
            return ValidationResult.builder()
                    .valid(false)
                    .errorMessages(errors)
                    .errorCode("TRANS_VAL_001")
                    .build();
        }
    }
    
    @Override
    public String getStrategyName() {
        return "TransactionRequestValidationStrategy";
    }
    
    @Override
    public int getOrder() {
        return 1;
    }
}

