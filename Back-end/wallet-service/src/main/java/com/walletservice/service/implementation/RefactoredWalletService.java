package com.walletservice.service.implementation;

import com.walletservice.dto.request.CreateWalletRequestDTO;
import com.walletservice.dto.request.UpdateBalanceRequestDTO;
import com.walletservice.dto.request.WalletUpdateRequest;
import com.walletservice.service.commands.CreateWalletCommand;
import com.walletservice.service.commands.UpdateBalanceCommand;
import com.walletservice.service.interfaces.WalletBalanceService;
import com.walletservice.service.interfaces.WalletManagementService;
import com.walletservice.service.interfaces.WalletQueryService;
import com.themoneywallet.sharedUtilities.patterns.command.CommandExecutor;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.service.BaseService;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Refactored Wallet Service following SOLID principles
 * Uses Command Pattern, Strategy Pattern, and separates concerns into focused interfaces
 */
@Service
@Slf4j
public class RefactoredWalletService extends BaseService 
    implements WalletManagementService, WalletBalanceService, WalletQueryService {
    
    private final CommandExecutor commandExecutor;
    private final CreateWalletCommand createWalletCommand;
    private final UpdateBalanceCommand updateBalanceCommand;
    private final WalletQueryService walletQueryService;
    
    public RefactoredWalletService(
            UnifidResponseHandler responseHandler,
            EventPublisher eventPublisher,
            CommandExecutor commandExecutor,
            CreateWalletCommand createWalletCommand,
            UpdateBalanceCommand updateBalanceCommand,
            WalletQueryService walletQueryService) {
        super(responseHandler, eventPublisher);
        this.commandExecutor = commandExecutor;
        this.createWalletCommand = createWalletCommand;
        this.updateBalanceCommand = updateBalanceCommand;
        this.walletQueryService = walletQueryService;
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> createWallet(CreateWalletRequestDTO request, String token) {
        // Add token to request for command processing
        request.setToken(token);
        
        return executeOperation("createWallet", request, input -> 
                commandExecutor.execute(createWalletCommand, input));
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> updateBalance(UpdateBalanceRequestDTO request, String token) {
        // Add token to request for command processing  
        request.setToken(token);
        
        return executeOperation("updateBalance", request, input -> 
                commandExecutor.execute(updateBalanceCommand, input));
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> reserveAmount(UpdateBalanceRequestDTO request, String token) {
        request.setOperationType(com.themoneywallet.sharedUtilities.enums.BalanceOperationType.RESERVE);
        return updateBalance(request, token);
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> releaseAmount(UpdateBalanceRequestDTO request, String token) {
        request.setOperationType(com.themoneywallet.sharedUtilities.enums.BalanceOperationType.RELEASE);
        return updateBalance(request, token);
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> getWallet(UUID walletId, String token) {
        return walletQueryService.getWallet(walletId, token);
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> getAllWallets(String token) {
        return walletQueryService.getAllWallets(token);
    }
    
    @Override
    public boolean walletExists(UUID userId, String walletType, String currency) {
        return walletQueryService.walletExists(userId, walletType, currency);
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> updateWallet(WalletUpdateRequest request, String token) {
        // TODO: Implement with UpdateWalletCommand following the same pattern
        throw new UnsupportedOperationException("To be implemented with UpdateWalletCommand");
    }
}

