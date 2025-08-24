package com.themoneywallet.transactionservice.service.commands;

import com.themoneywallet.transactionservice.dto.request.TransactionRequest;
import com.themoneywallet.transactionservice.dto.response.TransactionResponse;
import com.themoneywallet.transactionservice.entity.Transaction;
import com.themoneywallet.transactionservice.repository.TransactionRepository;
import com.themoneywallet.transactionservice.service.interfaces.TransactionValidationService;
import com.themoneywallet.transactionservice.domain.events.TransactionInitiatedEvent;
import com.themoneywallet.transactionservice.common.TransactionStatus;
import com.themoneywallet.sharedUtilities.patterns.command.Command;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.dto.response.ValidationResult;
import com.themoneywallet.sharedUtilities.service.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Command for transaction initiation
 * Follows Command Pattern and Single Responsibility Principle
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitiateTransactionCommand implements Command<TransactionRequest, TransactionResponse> {
    
    private final TransactionRepository transactionRepository;
    private final TransactionValidationService validationService;
    private final EventPublisher eventPublisher;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public TransactionResponse execute(TransactionRequest request) {
        log.info("Executing initiate transaction command for sender: {}", request.getSenderUserId());
        
        // Validate request
        ValidationResult validationResult = validationService.validateTransactionRequest(request);
        if (!validationResult.isValid()) {
            throw new ValidationException(
                    String.join(", ", validationResult.getErrorMessages()),
                    validationResult.getErrorCode()
            );
        }
        
        // Validate business rules
        ValidationResult businessValidation = validationService.validateBusinessRules(request);
        if (!businessValidation.isValid()) {
            throw new ValidationException(
                    String.join(", ", businessValidation.getErrorMessages()),
                    businessValidation.getErrorCode()
            );
        }
        
        // Create transaction
        Transaction transaction = createTransaction(request);
        
        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Publish domain event
        publishTransactionInitiatedEvent(savedTransaction);
        
        log.info("Transaction {} initiated successfully", savedTransaction.getTransactionId());
        return modelMapper.map(savedTransaction, TransactionResponse.class);
    }
    
    @Override
    public boolean canExecute(TransactionRequest input) {
        return input != null && 
               input.getSenderUserId() != null &&
               input.getAmount() != null &&
               input.getTransactionType() != null;
    }
    
    @Override
    public String getCommandName() {
        return "InitiateTransactionCommand";
    }
    
    private Transaction createTransaction(TransactionRequest request) {
        return Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .transactionType(request.getTransactionType())
                .senderUserId(request.getSenderUserId())
                .receiverUserId(request.getReceiverUserId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .referenceId(request.getReferenceId())
                .status(TransactionStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    private void publishTransactionInitiatedEvent(Transaction transaction) {
        TransactionInitiatedEvent event = new TransactionInitiatedEvent(
                transaction.getTransactionId(),
                transaction
        );
        
        eventPublisher.publish(event);
    }
}

