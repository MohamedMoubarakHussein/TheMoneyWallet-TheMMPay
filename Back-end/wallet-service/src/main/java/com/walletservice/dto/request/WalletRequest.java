package com.walletservice.dto.request;

import com.walletservice.entity.CurrencyType;
import com.walletservice.entity.WalletTypes;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class WalletRequest {
      private WalletTypes walletType; 
      
    @Enumerated(EnumType.STRING)
    private CurrencyType CurrencyType;
}
