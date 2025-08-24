package com.themoneywallet.sharedUtilities.patterns.observer;

/**
 * Generic event listener interface following Observer Pattern
 */
public interface EventListener<T extends DomainEvent> {
    
    /**
     * Handles the domain event
     * @param event the event to handle
     */
    void handle(T event);
    
    /**
     * Returns the event type this listener handles
     */
    Class<T> getEventType();
    
    /**
     * Returns the listener name for identification
     */
    String getListenerName();
    
    /**
     * Returns whether this listener should handle the event asynchronously
     */
    default boolean isAsync() {
        return true;
    }
    
    /**
     * Returns the order of execution when multiple listeners handle the same event
     * Lower values execute first
     */
    default int getOrder() {
        return 0;
    }
}

