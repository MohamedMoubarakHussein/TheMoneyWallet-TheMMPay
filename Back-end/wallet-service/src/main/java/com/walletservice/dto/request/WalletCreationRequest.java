package com.walletservice.dto.request;


import com.walletservice.entity.CurrencyType;

import com.walletservice.entity.WalletTypes;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WalletCreationRequest {
    
    @NotNull(message = "name cannot be null.")
    private String name;
    @NotNull(message = "CurrencyType cannot be null.")
    @Enumerated(EnumType.STRING)
    private CurrencyType currency;
    @NotNull(message = "walletType cannot be null.")
    @Enumerated(EnumType.STRING)
    private WalletTypes walletType; 
   
}
