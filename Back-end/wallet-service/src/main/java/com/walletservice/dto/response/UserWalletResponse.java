package com.walletservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.walletservice.entity.CurrencyType;
import com.walletservice.entity.WalletStatus;
import com.walletservice.entity.WalletTypes;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletResponse {

    private String id;
    private String userId;
    private String name;

    private WalletTypes type;

    private BigDecimal balance;

    private CurrencyType currency;

    private WalletStatus status;

    private String description;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastTransactionAt;
    private boolean isPrimary;

}