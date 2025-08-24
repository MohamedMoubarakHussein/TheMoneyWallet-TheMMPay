package com.themoneywallet.transactionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.transactionservice.event.TransactionEvent;
import com.themoneywallet.transactionservice.dto.event.UserValidationPayload;
import com.themoneywallet.transactionservice.dto.event.WalletOperationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-validated-events", groupId = "transaction-service-group")
    public void consumeUserValidatedEvent(String message) {
        try {
            var event = objectMapper.readValue(message, TransactionEvent.class);
            var payload = objectMapper.readValue(event.getPayload(), UserValidationPayload.class);
            log.info("Consumed UserValidatedEvent for transactionId: {}, isValid: {}", payload.transactionId(), payload.isValid());
            transactionService.processUserValidationResponse(payload.transactionId(), payload.isValid());
        } catch (Exception e) {
            log.error("Error consuming UserValidatedEvent: {}", message, e);
        }
    }

    @KafkaListener(topics = "wallet-balance-updated-events", groupId = "transaction-service-group")
    public void consumeWalletBalanceUpdatedEvent(String message) {
        try {
            var event = objectMapper.readValue(message, TransactionEvent.class);
            var payload = objectMapper.readValue(event.getPayload(), WalletOperationRequest.class);
            log.info("Consumed WalletBalanceUpdatedEvent for transactionId: {}, amount: {}", event.getTransactionId(), payload.amount());

            if (event.getEventType().equals("WalletDebitResponse")) {
                transactionService.processWalletDebitResponse(event.getTransactionId(), true);
            } else if (event.getEventType().equals("WalletCreditResponse")) {
                transactionService.processWalletCreditResponse(event.getTransactionId(), true);
            } else {
                log.warn("Unknown WalletBalanceUpdatedEvent type for transactionId: {}", event.getTransactionId());
            }

        } catch (Exception e) {
            log.error("Error consuming WalletBalanceUpdatedEvent: {}", message, e);
        }
    }
}