package com.themoneywallet.transactionservice.Exception;

public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
