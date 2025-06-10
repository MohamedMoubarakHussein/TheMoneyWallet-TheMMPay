package com.themoneywallet.dashboardservice.entity.fixed;
import java.math.BigDecimal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletLimits {
  
    private BigDecimal dailyTransactionLimit;
    private BigDecimal maxTransactionAmount;
    private BigDecimal maxBalance;
    private BigDecimal lowBalanceThreshold;
}