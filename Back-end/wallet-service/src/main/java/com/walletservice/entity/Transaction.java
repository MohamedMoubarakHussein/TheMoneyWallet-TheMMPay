package com.walletservice.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {

    private double amount;
    @Enumerated(EnumType.STRING)
    private CurrencyType CurrencyType;
    private long SenderUserId;
    private long SenderWalletId;
    private double SenderBalance;


}
