package com.walletservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.themoneywallet.sharedUtilities.enums.CurrencyType;
import com.themoneywallet.sharedUtilities.enums.WalletStatus;
import com.themoneywallet.sharedUtilities.enums.WalletTypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WalletResponseDTO {
    private Long walletId;
    private String walletNumber;
    private String userId;
    private WalletTypes walletType;
    private CurrencyType currency;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private WalletStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Override
    public String toString() {
      return "Long:walletId;String:walletNumber;String:userId;WalletTypes:walletType;CurrencyType:currency;BigDecimal:balance;BigDecimal:availableBalance;WalletStatus:status;LocalDateTime:createdAt;LocalDateTime:updatedAt;";
    }

}