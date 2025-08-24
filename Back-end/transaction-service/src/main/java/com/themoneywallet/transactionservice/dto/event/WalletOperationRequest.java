package com.themoneywallet.transactionservice.dto.event;

import java.math.BigDecimal;

public record WalletOperationRequest(
    String userId,
    BigDecimal amount,
    String currency
) {}