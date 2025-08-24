package com.themoneywallet.sharedUtilities.patterns.observer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Event publisher following Observer Pattern
 * Manages the publication of domain events to registered listeners
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {
    
    private final List<EventListener<? extends DomainEvent>> eventListeners;
    
    /**
     * Publishes a domain event to all registered listeners
     */
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void publish(T event) {
        log.info("Publishing event: {} with ID: {}", event.getEventType(), event.getEventId());
        
        List<EventListener<T>> matchingListeners = eventListeners.stream()
                .filter(listener -> listener.getEventType().isAssignableFrom(event.getClass()))
                .map(listener -> (EventListener<T>) listener)
                .sorted((l1, l2) -> Integer.compare(l1.getOrder(), l2.getOrder()))
                .collect(Collectors.toList());
        
        if (matchingListeners.isEmpty()) {
            log.debug("No listeners found for event type: {}", event.getEventType());
            return;
        }
        
        log.debug("Found {} listeners for event type: {}", matchingListeners.size(), event.getEventType());
        
        for (EventListener<T> listener : matchingListeners) {
            try {
                if (listener.isAsync()) {
                    handleAsync(listener, event);
                } else {
                    handleSync(listener, event);
                }
            } catch (Exception e) {
                log.error("Error notifying listener {} for event {}: {}", 
                        listener.getListenerName(), event.getEventId(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Handles event synchronously
     */
    private <T extends DomainEvent> void handleSync(EventListener<T> listener, T event) {
        log.debug("Handling event {} synchronously with listener: {}", 
                event.getEventId(), listener.getListenerName());
        listener.handle(event);
    }
    
    /**
     * Handles event asynchronously
     */
    @Async
    private <T extends DomainEvent> CompletableFuture<Void> handleAsync(EventListener<T> listener, T event) {
        log.debug("Handling event {} asynchronously with listener: {}", 
                event.getEventId(), listener.getListenerName());
        
        return CompletableFuture.runAsync(() -> {
            try {
                listener.handle(event);
                log.debug("Successfully handled event {} with listener: {}", 
                        event.getEventId(), listener.getListenerName());
            } catch (Exception e) {
                log.error("Error in async event handling for listener {} and event {}: {}", 
                        listener.getListenerName(), event.getEventId(), e.getMessage(), e);
            }
        });
    }
}

