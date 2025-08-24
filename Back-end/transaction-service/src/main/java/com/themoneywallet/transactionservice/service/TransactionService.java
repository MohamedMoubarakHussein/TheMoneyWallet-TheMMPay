package com.themoneywallet.transactionservice.service;

import com.themoneywallet.transactionservice.dto.request.TransactionRequest;
import com.themoneywallet.transactionservice.dto.response.TransactionResponse;
import com.themoneywallet.transactionservice.dto.status.TransactionStatusResponse;

public interface TransactionService {
    TransactionResponse initiateTransaction(TransactionRequest request);
    TransactionStatusResponse getTransactionStatus(String transactionId);
    void processWalletDebitResponse(String transactionId, boolean success);
    void processWalletCreditResponse(String transactionId, boolean success);
    void processUserValidationResponse(String transactionId, boolean isValid);
}
