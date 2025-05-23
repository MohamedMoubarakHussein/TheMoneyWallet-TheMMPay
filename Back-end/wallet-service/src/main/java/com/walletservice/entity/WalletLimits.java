package com.walletservice.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class WalletLimits {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
     private long id;
    private BigDecimal dailyTransactionLimit;
    private BigDecimal maxTransactionAmount;
    private BigDecimal maxBalance;
    private BigDecimal lowBalanceThreshold;
}