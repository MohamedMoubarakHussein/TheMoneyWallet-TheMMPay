package com.themoneywallet.transactionservice.dto.event;

import java.math.BigDecimal;

public record NotificationRequest(
    String transactionId,
    String userId,
    String status,
    BigDecimal amount,
    String currency
) {}