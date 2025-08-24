package com.themoneywallet.transactionservice.dto.event;

import com.themoneywallet.transactionservice.common.TransactionStatus;
import com.themoneywallet.transactionservice.dto.request.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistoryRecordPayload(
    String transactionId,
    TransactionType transactionType,
    String senderUserId,
    String receiverUserId,
    BigDecimal amount,
    String currency,
    String description,
    String referenceId,
    TransactionStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}