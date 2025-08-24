package com.walletservice.service.interfaces;

import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Interface for wallet query operations
 * Follows Interface Segregation Principle
 */
public interface WalletQueryService {
    
    /**
     * Retrieves a specific wallet by ID
     */
    ResponseEntity<UnifiedResponse> getWallet(UUID walletId, String token);
    
    /**
     * Retrieves all wallets for a user
     */
    ResponseEntity<UnifiedResponse> getAllWallets(String token);
    
    /**
     * Checks if wallet exists for user with specific type and currency
     */
    boolean walletExists(UUID userId, String walletType, String currency);
}
