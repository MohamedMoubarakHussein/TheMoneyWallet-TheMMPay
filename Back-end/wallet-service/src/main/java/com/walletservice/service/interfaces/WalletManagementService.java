package com.walletservice.service.interfaces;

import com.walletservice.dto.request.CreateWalletRequestDTO;
import com.walletservice.dto.request.WalletUpdateRequest;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import org.springframework.http.ResponseEntity;

/**
 * Interface for wallet management operations
 * Follows Interface Segregation Principle
 */
public interface WalletManagementService {
    
    /**
     * Creates a new wallet
     */
    ResponseEntity<UnifiedResponse> createWallet(CreateWalletRequestDTO request, String token);
    
    /**
     * Updates wallet information
     */
    ResponseEntity<UnifiedResponse> updateWallet(WalletUpdateRequest request, String token);
}

