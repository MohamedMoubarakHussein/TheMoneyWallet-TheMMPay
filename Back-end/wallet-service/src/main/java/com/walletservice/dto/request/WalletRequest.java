package com.walletservice.dto.request;

import com.walletservice.entity.CurrencyType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class WalletRequest {
    
    @Enumerated(EnumType.STRING)
    private CurrencyType CurrencyType;
}
