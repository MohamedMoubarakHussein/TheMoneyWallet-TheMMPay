package com.walletservice.domain.events;

import com.themoneywallet.sharedUtilities.patterns.observer.DomainEvent;
import com.walletservice.entity.Wallet;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Domain event for wallet balance changes
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WalletBalanceChangedEvent extends DomainEvent {
    
    private final Wallet wallet;
    private final BigDecimal previousBalance;
    private final BigDecimal newBalance;
    private final String operationType;
    
    public WalletBalanceChangedEvent(String userId, Wallet wallet, 
                                   BigDecimal previousBalance, BigDecimal newBalance, 
                                   String operationType) {
        super(userId);
        this.wallet = wallet;
        this.previousBalance = previousBalance;
        this.newBalance = newBalance;
        this.operationType = operationType;
    }
    
    @Override
    public Object getEventData() {
        return this;
    }
}

