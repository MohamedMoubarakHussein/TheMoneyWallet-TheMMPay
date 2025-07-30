package com.walletservice.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.themoneywallet.sharedUtilities.enums.BalanceOperationType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateBalanceRequestDTO {
    @NotBlank(message = "Wallet Id is required")
    private UUID walletId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 17, fraction = 2)
    private BigDecimal amount;
    
    @NotNull(message = "Operation type is required")
    private BalanceOperationType operationType; 
    
    @NotBlank(message = "Transaction reference is required")
    private String transactionReference;
    
    private String description;
}