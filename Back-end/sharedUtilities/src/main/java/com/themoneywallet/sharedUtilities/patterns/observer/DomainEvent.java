package com.themoneywallet.sharedUtilities.patterns.observer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for domain events following Observer Pattern
 * Provides common structure for all events in the system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DomainEvent {
    
    private String eventId = UUID.randomUUID().toString();
    private LocalDateTime occurredAt = LocalDateTime.now();
    private String aggregateId;
    private String eventType;
    private Integer version = 1;
    
    /**
     * Constructor with aggregate ID
     */
    public DomainEvent(String aggregateId) {
        this.aggregateId = aggregateId;
        this.eventType = this.getClass().getSimpleName();
    }
    
    /**
     * Returns the event data as an object
     */
    public abstract Object getEventData();
}

