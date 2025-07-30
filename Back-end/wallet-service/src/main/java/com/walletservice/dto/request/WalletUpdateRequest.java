package com.walletservice.dto.request;



import java.util.UUID;

import com.themoneywallet.sharedUtilities.enums.WalletStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletUpdateRequest {
    private UUID walletId;
    private WalletStatus status; 
    private boolean isPrimary;  
}
