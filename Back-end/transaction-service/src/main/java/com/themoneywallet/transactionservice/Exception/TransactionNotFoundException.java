package com.themoneywallet.transactionservice.Exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
