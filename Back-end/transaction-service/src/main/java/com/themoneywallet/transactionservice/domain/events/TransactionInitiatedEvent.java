package com.themoneywallet.transactionservice.domain.events;

import com.themoneywallet.sharedUtilities.patterns.observer.DomainEvent;
import com.themoneywallet.transactionservice.entity.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Domain event for transaction initiation
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionInitiatedEvent extends DomainEvent {
    
    private final Transaction transaction;
    
    public TransactionInitiatedEvent(String transactionId, Transaction transaction) {
        super(transactionId);
        this.transaction = transaction;
    }
    
    @Override
    public Object getEventData() {
        return transaction;
    }
}

