package com.themoneywallet.dashboardservice.entity;
import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WalletLimits {
  @Id
  private String id;
    private BigDecimal dailyTransactionLimit;
    private BigDecimal maxTransactionAmount;
    private BigDecimal maxBalance;
    private BigDecimal lowBalanceThreshold;

    @PrePersist
    public void setup(){
      this.id = UUID.randomUUID().toString();
    }
}