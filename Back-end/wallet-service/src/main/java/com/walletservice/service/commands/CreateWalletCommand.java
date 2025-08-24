package com.walletservice.service.commands;

import com.walletservice.dto.request.CreateWalletRequestDTO;
import com.walletservice.entity.Wallet;
import com.walletservice.entity.WalletLimits;
import com.walletservice.repository.WalletRepository;
import com.walletservice.domain.events.WalletCreatedEvent;
import com.themoneywallet.sharedUtilities.patterns.command.Command;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.enums.WalletStatus;
import com.themoneywallet.sharedUtilities.utilities.JwtValidator;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.themoneywallet.sharedUtilities.service.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command for wallet creation operation
 * Follows Command Pattern and Single Responsibility Principle
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateWalletCommand implements Command<CreateWalletRequestDTO, ResponseEntity<UnifiedResponse>> {
    
    private final WalletRepository walletRepository;
    private final JwtValidator jwtValidator;
    private final UnifidResponseHandler responseHandler;
    private final EventPublisher eventPublisher;
    
    // Default wallet limits - should be configurable
    private static final BigDecimal DEFAULT_DAILY_LIMIT = new BigDecimal("1000");
    private static final BigDecimal DEFAULT_MAX_BALANCE = new BigDecimal("50000");
    private static final BigDecimal DEFAULT_MAX_TRANSACTION = new BigDecimal("5000");
    private static final BigDecimal DEFAULT_LOW_BALANCE_THRESHOLD = new BigDecimal("10");
    
    @Override
    public ResponseEntity<UnifiedResponse> execute(CreateWalletRequestDTO request) {
        log.info("Executing create wallet command for wallet type: {}", request.getWalletType());
        
        UUID userId = jwtValidator.getUserId(request.getToken());
        
        // Check if wallet already exists
        if (walletRepository.existsByUserIdAndWalletTypeAndCurrency(
                userId, request.getWalletType(), request.getCurrency())) {
            throw new BusinessException(
                    "User already has a wallet of this type and currency", 
                    "WALLET_EXISTS");
        }
        
        // Create wallet
        Wallet wallet = createWallet(userId, request);
        
        // Save wallet
        Wallet savedWallet = walletRepository.save(wallet);
        
        // Publish domain event
        publishWalletCreatedEvent(savedWallet);
        
        return responseHandler.generateSuccessResponse("data", savedWallet, HttpStatus.CREATED);
    }
    
    @Override
    public boolean canExecute(CreateWalletRequestDTO input) {
        return input != null && 
               input.getWalletType() != null && 
               input.getCurrency() != null &&
               input.getToken() != null;
    }
    
    @Override
    public String getCommandName() {
        return "CreateWalletCommand";
    }
    
    private Wallet createWallet(UUID userId, CreateWalletRequestDTO request) {
        boolean isPrimary = walletRepository.countByUserId(userId) == 0;
        
        return Wallet.builder()
                .userId(userId)
                .walletId(UUID.randomUUID())
                .walletType(request.getWalletType())
                .currency(request.getCurrency())
                .balance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .reservedBalance(BigDecimal.ZERO)
                .status(WalletStatus.ACTIVE)
                .isPrimary(isPrimary)
                .transactionCount(0)
                .limits(createDefaultLimits())
                .lastTransactionAt(null)
                .build();
    }
    
    private WalletLimits createDefaultLimits() {
        return WalletLimits.builder()
                .dailyTransactionLimit(DEFAULT_DAILY_LIMIT)
                .maxTransactionAmount(DEFAULT_MAX_TRANSACTION)
                .maxBalance(DEFAULT_MAX_BALANCE)
                .lowBalanceThreshold(DEFAULT_LOW_BALANCE_THRESHOLD)
                .build();
    }
    
    private void publishWalletCreatedEvent(Wallet wallet) {
        WalletCreatedEvent event = new WalletCreatedEvent(
                wallet.getUserId().toString(), 
                wallet
        );
        
        eventPublisher.publish(event);
    }
}

