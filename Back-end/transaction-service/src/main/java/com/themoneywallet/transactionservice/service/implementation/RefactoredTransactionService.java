package com.themoneywallet.transactionservice.service.implementation;

import com.themoneywallet.transactionservice.dto.request.TransactionRequest;
import com.themoneywallet.transactionservice.dto.response.TransactionResponse;
import com.themoneywallet.transactionservice.dto.status.TransactionStatusResponse;
import com.themoneywallet.transactionservice.service.commands.InitiateTransactionCommand;
import com.themoneywallet.transactionservice.service.interfaces.TransactionQueryService;
import com.themoneywallet.transactionservice.service.interfaces.TransactionProcessingService;
import com.themoneywallet.transactionservice.service.TransactionService;
import com.themoneywallet.sharedUtilities.patterns.command.CommandExecutor;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.service.BaseService;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Refactored Transaction Service following SOLID principles
 * Uses Command Pattern, separates concerns into focused interfaces
 */
@Service
@Slf4j
public class RefactoredTransactionService extends BaseService implements TransactionService {
    
    private final CommandExecutor commandExecutor;
    private final InitiateTransactionCommand initiateTransactionCommand;
    private final TransactionQueryService transactionQueryService;
    private final TransactionProcessingService transactionProcessingService;
    
    public RefactoredTransactionService(
            UnifidResponseHandler responseHandler,
            EventPublisher eventPublisher,
            CommandExecutor commandExecutor,
            InitiateTransactionCommand initiateTransactionCommand,
            TransactionQueryService transactionQueryService,
            TransactionProcessingService transactionProcessingService) {
        super(responseHandler, eventPublisher);
        this.commandExecutor = commandExecutor;
        this.initiateTransactionCommand = initiateTransactionCommand;
        this.transactionQueryService = transactionQueryService;
        this.transactionProcessingService = transactionProcessingService;
    }
    
    @Override
    public TransactionResponse initiateTransaction(TransactionRequest request) {
        return commandExecutor.execute(initiateTransactionCommand, request);
    }
    
    @Override
    public TransactionStatusResponse getTransactionStatus(String transactionId) {
        return transactionQueryService.getTransactionStatus(transactionId);
    }
    
    @Override
    public void processWalletDebitResponse(String transactionId, boolean success) {
        executeOperation("processWalletDebitResponse", transactionId, input -> {
            transactionProcessingService.processWalletDebitResponse(input, success);
            return null;
        });
    }
    
    @Override
    public void processWalletCreditResponse(String transactionId, boolean success) {
        executeOperation("processWalletCreditResponse", transactionId, input -> {
            transactionProcessingService.processWalletCreditResponse(input, success);
            return null;
        });
    }
    
    @Override
    public void processUserValidationResponse(String transactionId, boolean isValid) {
        executeOperation("processUserValidationResponse", transactionId, input -> {
            transactionProcessingService.processUserValidationResponse(input, isValid);
            return null;
        });
    }
}
