package com.themoneywallet.dashboardservice.event.eventDTO;


import java.math.BigDecimal;

import java.time.LocalDateTime;


import com.themoneywallet.dashboardservice.entity.fixed.WalletLimits;

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

   
    private BigDecimal balance;

    
    private String CurrencyType;

    private LocalDateTime creationDate;
    private LocalDateTime updatedAt;
    private Integer transactionCount;

    
    private String walletType;


    private WalletLimits limits;

    private String status;


}
