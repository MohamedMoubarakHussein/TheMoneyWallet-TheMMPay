package com.walletservice.service.commands;

import com.walletservice.dto.request.UpdateBalanceRequestDTO;
import com.walletservice.dto.response.BalanceUpdateResponseDTO;
import com.walletservice.entity.Wallet;
import com.walletservice.repository.WalletRepository;
import com.walletservice.service.strategy.BalanceOperationStrategy;
import com.walletservice.service.factory.BalanceOperationStrategyFactory;
import com.walletservice.domain.events.WalletBalanceChangedEvent;
import com.themoneywallet.sharedUtilities.patterns.command.Command;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import com.themoneywallet.sharedUtilities.enums.WalletStatus;
import com.themoneywallet.sharedUtilities.utilities.JwtValidator;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.themoneywallet.sharedUtilities.service.BusinessException;
import com.themoneywallet.sharedUtilities.service.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Command for balance update operations
 * Follows Command Pattern and uses Strategy Pattern for different operations
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateBalanceCommand implements Command<UpdateBalanceRequestDTO, ResponseEntity<UnifiedResponse>> {
    
    private final WalletRepository walletRepository;
    private final JwtValidator jwtValidator;
    private final UnifidResponseHandler responseHandler;
    private final EventPublisher eventPublisher;
    private final BalanceOperationStrategyFactory strategyFactory;
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<UnifiedResponse> execute(UpdateBalanceRequestDTO request) {
        log.info("Executing update balance command for wallet: {}, operation: {}", 
                request.getWalletId(), request.getOperationType());
        
        UUID userId = jwtValidator.getUserId(request.getToken());
        
        // Find wallet
        Wallet wallet = findWallet(userId, request.getWalletId());
        
        // Validate wallet status
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new BusinessException("Wallet is not active", "WALLET_INACTIVE");
        }
        
        // Get strategy for operation type
        BalanceOperationStrategy strategy = strategyFactory.create(request.getOperationType().toString());
        
        // Validate operation
        ValidationResult validationResult = strategy.validateOperation(wallet, request);
        if (!validationResult.isValid()) {
            throw new ValidationException(
                    validationResult.getErrorMessages().get(0), 
                    validationResult.getErrorCode());
        }
        
        // Store previous balance for event
        BigDecimal previousBalance = wallet.getBalance();
        
        // Perform operation
        strategy.performOperation(wallet, request);
        
        // Save wallet
        Wallet savedWallet = walletRepository.save(wallet);
        
        // Publish domain event
        publishBalanceChangedEvent(savedWallet, previousBalance, request.getOperationType().toString());
        
        // Create response
        BalanceUpdateResponseDTO response = new BalanceUpdateResponseDTO();
        BeanUtils.copyProperties(savedWallet, response);
        
        return responseHandler.generateSuccessResponse("data", response, HttpStatus.OK);
    }
    
    @Override
    public boolean canExecute(UpdateBalanceRequestDTO input) {
        return input != null && 
               input.getWalletId() != null && 
               input.getOperationType() != null &&
               input.getAmount() != null &&
               input.getToken() != null;
    }
    
    @Override
    public String getCommandName() {
        return "UpdateBalanceCommand";
    }
    
    private Wallet findWallet(UUID userId, UUID walletId) {
        Optional<Wallet> opWallet = walletRepository.findByUserIdAndWalletId(userId, walletId);
        
        if (opWallet.isEmpty()) {
            throw new BusinessException("Wallet not found", "WALLET_NOT_FOUND");
        }
        
        return opWallet.get();
    }
    
    private void publishBalanceChangedEvent(Wallet wallet, BigDecimal previousBalance, String operationType) {
        WalletBalanceChangedEvent event = new WalletBalanceChangedEvent(
                wallet.getUserId().toString(),
                wallet,
                previousBalance,
                wallet.getBalance(),
                operationType
        );
        
        eventPublisher.publish(event);
    }
}

