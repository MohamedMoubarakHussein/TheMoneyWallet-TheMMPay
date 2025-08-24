package com.themoneywallet.transactionservice.service;

import com.themoneywallet.transactionservice.Exception.InvalidTransactionException;
import com.themoneywallet.transactionservice.Exception.TransactionNotFoundException;
import com.themoneywallet.transactionservice.common.TransactionStatus;
import com.themoneywallet.transactionservice.dto.request.TransactionRequest;
import com.themoneywallet.transactionservice.dto.response.TransactionResponse;
import com.themoneywallet.transactionservice.dto.status.TransactionStatusResponse;
import com.themoneywallet.transactionservice.entity.Transaction;
import com.themoneywallet.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public TransactionResponse initiateTransaction(TransactionRequest request) {
        log.info("Initiating transaction for sender: {} to receiver: {} with amount: {}",
                request.getSenderUserId(), request.getReceiverUserId(), request.getAmount());

        // Basic validation
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Transaction amount must be positive.");
        }

        // Create a new transaction entity
        Transaction transaction = Transaction.builder()
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

        transactionRepository.save(transaction);
        log.info("Transaction {} initiated and saved with status: {}", transaction.getTransactionId(), transaction.getStatus());

        // Publish TransactionInitiatedEvent to Kafka
        kafkaProducerService.sendTransactionInitiatedEvent(transaction.getTransactionId(), request);

        return modelMapper.map(transaction, TransactionResponse.class);
    }

    @Override
    public TransactionStatusResponse getTransactionStatus(String transactionId) {
        log.info("Fetching status for transaction ID: {}", transactionId);
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));

        return modelMapper.map(transaction, TransactionStatusResponse.class);
    }

    @Override
    @Transactional
    public void processWalletDebitResponse(String transactionId, boolean success) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));

        if (success) {
            log.info("Wallet debit successful for transaction: {}", transactionId);
            // If debit is successful, proceed with credit or mark as processing
            transaction.setStatus(TransactionStatus.PROCESSING);
            // For a transfer, next step would be to request credit for receiver
            if (transaction.getTransactionType() == com.themoneywallet.transactionservice.dto.request.TransactionType.TRANSFER) {
                kafkaProducerService.sendWalletCreditRequestEvent(transaction.getTransactionId(), transaction.getReceiverUserId(), transaction.getAmount(), transaction.getCurrency());
            } else {
                transaction.setStatus(TransactionStatus.COMPLETED);
                kafkaProducerService.sendTransactionCompletedEvent(transaction.getTransactionId());
                kafkaProducerService.sendTransactionHistoryRecordEvent(transaction);
                kafkaProducerService.sendTransactionNotificationRequestEvent(transaction.getTransactionId(), transaction.getSenderUserId(), transaction.getStatus().value(), transaction.getAmount(), transaction.getCurrency());
            }
        } else {
            log.error("Wallet debit failed for transaction: {}", transactionId);
            transaction.setStatus(TransactionStatus.FAILED);
            kafkaProducerService.sendTransactionFailedEvent(transaction.getTransactionId(), "Wallet debit failed.");
            kafkaProducerService.sendTransactionHistoryRecordEvent(transaction);
            kafkaProducerService.sendTransactionNotificationRequestEvent(transaction.getTransactionId(), transaction.getSenderUserId(), transaction.getStatus().value(), transaction.getAmount(), transaction.getCurrency());
        }
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void processWalletCreditResponse(String transactionId, boolean success) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));

        if (success) {
            log.info("Wallet credit successful for transaction: {}", transactionId);
            transaction.setStatus(TransactionStatus.COMPLETED);
            kafkaProducerService.sendTransactionCompletedEvent(transaction.getTransactionId());
        } else {
            log.error("Wallet credit failed for transaction: {}", transactionId);
            log.error("Wallet credit failed for transaction: {}. Initiating compensation for sender.", transactionId);
            transaction.setStatus(TransactionStatus.FAILED);
            kafkaProducerService.sendTransactionFailedEvent(transaction.getTransactionId(), "Wallet credit failed. Compensation initiated.");
            // Compensation: Credit the amount back to the sender's wallet
            kafkaProducerService.sendWalletCreditRequestEvent(transaction.getTransactionId(), transaction.getSenderUserId(), transaction.getAmount(), transaction.getCurrency());
        }
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        kafkaProducerService.sendTransactionHistoryRecordEvent(transaction);
        kafkaProducerService.sendTransactionNotificationRequestEvent(transaction.getTransactionId(), transaction.getReceiverUserId(), transaction.getStatus().value(), transaction.getAmount(), transaction.getCurrency());
    }

    @Override
    @Transactional
    public void processUserValidationResponse(String transactionId, boolean isValid) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));

        if (isValid) {
            log.info("User validation successful for transaction: {}", transactionId);
            // User is valid, now request wallet debit
            kafkaProducerService.sendWalletDebitRequestEvent(transaction.getTransactionId(), transaction.getSenderUserId(), transaction.getAmount(), transaction.getCurrency());
        } else {
            log.error("User validation failed for transaction: {}", transactionId);
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);
            kafkaProducerService.sendTransactionFailedEvent(transaction.getTransactionId(), "User validation failed.");
            kafkaProducerService.sendTransactionHistoryRecordEvent(transaction);
            kafkaProducerService.sendTransactionNotificationRequestEvent(transaction.getTransactionId(), transaction.getSenderUserId(), transaction.getStatus().value(), transaction.getAmount(), transaction.getCurrency());
        }
    }
}
