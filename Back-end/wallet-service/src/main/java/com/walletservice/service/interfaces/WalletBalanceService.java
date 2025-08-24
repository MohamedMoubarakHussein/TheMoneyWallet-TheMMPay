package com.walletservice.service.interfaces;

import com.walletservice.dto.request.UpdateBalanceRequestDTO;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import org.springframework.http.ResponseEntity;

/**
 * Interface for wallet balance operations
 * Follows Interface Segregation Principle
 */
public interface WalletBalanceService {
    
    /**
     * Updates wallet balance with the specified operation
     */
    ResponseEntity<UnifiedResponse> updateBalance(UpdateBalanceRequestDTO request, String token);
    
    /**
     * Reserves amount in wallet
     */
    ResponseEntity<UnifiedResponse> reserveAmount(UpdateBalanceRequestDTO request, String token);
    
    /**
     * Releases reserved amount in wallet
     */
    ResponseEntity<UnifiedResponse> releaseAmount(UpdateBalanceRequestDTO request, String token);
}

