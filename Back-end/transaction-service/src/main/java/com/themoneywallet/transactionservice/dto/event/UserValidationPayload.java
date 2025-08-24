package com.themoneywallet.transactionservice.dto.event;

public record UserValidationPayload(
    String transactionId,
    boolean isValid
) {}