package com.themoneywallet.authenticationservice.domain.events;

import com.themoneywallet.sharedUtilities.patterns.observer.DomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Domain event for user sign up
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSignedUpEvent extends DomainEvent {
    
    private final String email;
    private final String userName;
    private final String firstName;
    private final String lastName;
    private final String userRole;
    private final String emailVerificationToken;
    
    public UserSignedUpEvent(String userId, String email, String userName, 
                           String firstName, String lastName, String userRole, 
                           String emailVerificationToken) {
        super(userId);
        this.email = email;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRole = userRole;
        this.emailVerificationToken = emailVerificationToken;
    }
    
    @Override
    public Object getEventData() {
        return this;
    }
}

