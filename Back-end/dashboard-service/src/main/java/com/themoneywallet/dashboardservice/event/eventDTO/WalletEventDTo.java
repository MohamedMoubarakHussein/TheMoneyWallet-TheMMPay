package com.themoneywallet.dashboardservice.event.eventDTO;


import java.math.BigDecimal;

import java.time.LocalDateTime;

import com.themoneywallet.dashboardservice.entity.WalletLimits;
import com.themoneywallet.dashboardservice.entity.fixed.CurrencyType;
import com.themoneywallet.dashboardservice.entity.fixed.WalletStatus;
import com.themoneywallet.dashboardservice.entity.fixed.WalletTypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class WalletEventDTo {

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
