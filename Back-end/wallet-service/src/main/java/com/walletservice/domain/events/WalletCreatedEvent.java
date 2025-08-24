package com.walletservice.domain.events;

import com.themoneywallet.sharedUtilities.patterns.observer.DomainEvent;
import com.walletservice.entity.Wallet;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Domain event for wallet creation
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WalletCreatedEvent extends DomainEvent {
    
    private final Wallet wallet;
    
    public WalletCreatedEvent(String userId, Wallet wallet) {
        super(userId);
        this.wallet = wallet;
    }
    
    @Override
    public Object getEventData() {
        return wallet;
    }
}

