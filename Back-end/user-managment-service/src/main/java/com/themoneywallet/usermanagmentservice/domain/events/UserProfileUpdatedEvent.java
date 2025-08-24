package com.themoneywallet.usermanagmentservice.domain.events;

import com.themoneywallet.sharedUtilities.patterns.observer.DomainEvent;
import com.themoneywallet.usermanagmentservice.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Domain event for user profile updates
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserProfileUpdatedEvent extends DomainEvent {
    
    private final User user;
    
    public UserProfileUpdatedEvent(String userId, User user) {
        super(userId);
        this.user = user;
    }
    
    @Override
    public Object getEventData() {
        return user;
    }
}

