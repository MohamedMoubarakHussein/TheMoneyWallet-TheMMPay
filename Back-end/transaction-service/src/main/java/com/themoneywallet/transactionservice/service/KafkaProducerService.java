package com.themoneywallet.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.transactionservice.entity.Transaction;
import com.themoneywallet.transactionservice.dto.request.TransactionRequest;
import com.themoneywallet.transactionservice.event.TransactionEvent;
import com.themoneywallet.transactionservice.dto.event.NotificationRequest;
import com.themoneywallet.transactionservice.dto.event.TransactionFailureDetails;
import com.themoneywallet.transactionservice.dto.event.TransactionHistoryRecordPayload;
import com.themoneywallet.transactionservice.dto.event.WalletOperationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    private static final String TRANSACTION_INITIATED_TOPIC = "transaction-initiated-events";
    private static final String WALLET_DEBIT_REQUEST_TOPIC = "wallet-debit-request-events";
    private static final String WALLET_CREDIT_REQUEST_TOPIC = "wallet-credit-request-events";
    private static final String TRANSACTION_PROCESSED_TOPIC = "transaction-processed-events";
    private static final String TRANSACTION_FAILED_TOPIC = "transaction-failed-events";
    private static final String TRANSACTION_COMPLETED_TOPIC = "transaction-completed-events";
    private static final String TRANSACTION_NOTIFICATION_REQUEST_TOPIC = "transaction-notification-request-events";
    private static final String TRANSACTION_HISTORY_RECORD_TOPIC = "transaction-history-record-events";

    public void sendTransactionInitiatedEvent(String transactionId, TransactionRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            TransactionEvent event = createTransactionEvent(transactionId, "TransactionInitiated", payload);
            kafkaTemplate.send(TRANSACTION_INITIATED_TOPIC, transactionId, objectMapper.writeValueAsString(event));
            log.info("Sent TransactionInitiatedEvent for transactionId: {}", transactionId);
        } catch (JsonProcessingException e) {
            log.error("Error sending TransactionInitiatedEvent for transactionId: {}", transactionId, e);
        }
    }

    public void sendWalletDebitRequestEvent(String transactionId, String userId, BigDecimal amount, String currency) {
        try {
            var walletOperationRequest = new WalletOperationRequest(userId, amount, currency);
            String payload = objectMapper.writeValueAsString(walletOperationRequest);
            TransactionEvent event = createTransactionEvent(transactionId, "WalletDebitRequest", payload);
            kafkaTemplate.send(WALLET_DEBIT_REQUEST_TOPIC, transactionId, objectMapper.writeValueAsString(event));
            log.info("Sent WalletDebitRequestEvent for transactionId: {}", transactionId);
        } catch (JsonProcessingException e) {
            log.error("Error sending WalletDebitRequestEvent for transactionId: {}", transactionId, e);
        }
    }

    public void sendWalletCreditRequestEvent(String transactionId, String userId, BigDecimal amount, String currency) {
        try {
            var walletOperationRequest = new WalletOperationRequest(userId, amount, currency);
            String payload = objectMapper.writeValueAsString(walletOperationRequest);
            TransactionEvent event = createTransactionEvent(transactionId, "WalletCreditRequest", payload);
            kafkaTemplate.send(WALLET_CREDIT_REQUEST_TOPIC, transactionId, objectMapper.writeValueAsString(event));
            log.info("Sent WalletCreditRequestEvent for transactionId: {}", transactionId);
        } catch (JsonProcessingException e) {
            log.error("Error sending WalletCreditRequestEvent for transactionId: {}", transactionId, e);
        }
    }

    public void sendTransactionProcessedEvent(String transactionId) {
        try {
            TransactionEvent event = createTransactionEvent(transactionId, "TransactionProcessed", "{}");
            kafkaTemplate.send(TRANSACTION_PROCESSED_TOPIC, transactionId, objectMapper.writeValueAsString(event));
            log.info("Sent TransactionProcessedEvent for transactionId: {}", transactionId);
        } catch (JsonProcessingException e) {
            log.error("Error sending TransactionProcessedEvent for transactionId: {}", transactionId, e);
        }
    }

    public void sendTransactionFailedEvent(String transactionId, String reason) {
        try {
            var failureDetails = new TransactionFailureDetails(reason);
            String payload = objectMapper.writeValueAsString(failureDetails);
            TransactionEvent event = createTransactionEvent(transactionId, "TransactionFailed", payload);
            kafkaTemplate.send(TRANSACTION_FAILED_TOPIC, transactionId, objectMapper.writeValueAsString(event));
            log.info("Sent TransactionFailedEvent for transactionId: {}", transactionId);
        } catch (JsonProcessingException e) {
            log.error("Error sending TransactionFailedEvent for transactionId: {}", transactionId, e);
        }
    }

    public void sendTransactionCompletedEvent(String transactionId) {
        try {
            TransactionEvent event = createTransactionEvent(transactionId, "TransactionCompleted", "{}");
            kafkaTemplate.send(TRANSACTION_COMPLETED_TOPIC, transactionId, objectMapper.writeValueAsString(event));
            log.info("Sent TransactionCompletedEvent for transactionId: {}", transactionId);
        } catch (JsonProcessingException e) {
            log.error("Error sending TransactionCompletedEvent for transactionId: {}", transactionId, e);
        }
    }

    public void sendTransactionNotificationRequestEvent(String transactionId, String userId, String status, BigDecimal amount, String currency) {
        try {
            var notificationRequest = new NotificationRequest(transactionId, userId, status, amount, currency);
            String payload = objectMapper.writeValueAsString(notificationRequest);
            TransactionEvent event = createTransactionEvent(transactionId, "TransactionNotificationRequest", payload);
            kafkaTemplate.send(TRANSACTION_NOTIFICATION_REQUEST_TOPIC, transactionId, objectMapper.writeValueAsString(event));
            log.info("Sent TransactionNotificationRequestEvent for transactionId: {}", transactionId);
        } catch (JsonProcessingException e) {
            log.error("Error sending TransactionNotificationRequestEvent for transactionId: {}", transactionId, e);
        }
    }

    public void sendTransactionHistoryRecordEvent(Transaction transaction) {
        try {
            var historyPayload = modelMapper.map(transaction, TransactionHistoryRecordPayload.class);
            String payload = objectMapper.writeValueAsString(historyPayload);
            TransactionEvent event = createTransactionEvent(transaction.getTransactionId(), "TransactionHistoryRecord", payload);
            kafkaTemplate.send(TRANSACTION_HISTORY_RECORD_TOPIC, transaction.getTransactionId(), objectMapper.writeValueAsString(event));
            log.info("Sent TransactionHistoryRecordEvent for transactionId: {}", transaction.getTransactionId());
        } catch (JsonProcessingException e) {
            log.error("Error sending TransactionHistoryRecordEvent for transactionId: {}", transaction.getTransactionId(), e);
        }
    }

    private TransactionEvent createTransactionEvent(String transactionId, String eventType, String payload) {
        TransactionEvent event = new TransactionEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(eventType);

        // Convert LocalDateTime to XMLGregorianCalendar
        GregorianCalendar gregorianCalendar = GregorianCalendar.from(LocalDateTime.now().atZone(ZoneId.systemDefault()));
        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            event.setTimestamp(xmlGregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            log.error("Error converting LocalDateTime to XMLGregorianCalendar", e);
            // Handle error, perhaps set timestamp to null or throw a runtime exception
        }

        event.setTransactionId(transactionId);
        event.setPayload(payload);
        return event;
    }
}