package com.themoneywallet.authenticationservice.domain.events;

import com.themoneywallet.sharedUtilities.patterns.observer.DomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Domain event for user sign in
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSignedInEvent extends DomainEvent {
    
    private final String email;
    private final String userRole;
    
    public UserSignedInEvent(String userId, String email, String userRole) {
        super(userId);
        this.email = email;
        this.userRole = userRole;
    }
    
    @Override
    public Object getEventData() {
        return this;
    }
}

