package com.themoneywallet.sharedUtilities.patterns.chain;

import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base class for chain handlers
 * Provides common chain management functionality
 */
@Slf4j
public abstract class AbstractChainHandler<T, R> implements ChainHandler<T, R> {
    
    private ChainHandler<T, R> nextHandler;
    
    @Override
    public void setNext(ChainHandler<T, R> nextHandler) {
        this.nextHandler = nextHandler;
    }
    
    @Override
    public R handle(T request) {
        log.debug("Handler {} checking request", getHandlerName());
        
        if (canHandle(request)) {
            log.debug("Handler {} processing request", getHandlerName());
            return doHandle(request);
        } else if (nextHandler != null) {
            log.debug("Handler {} passing request to next handler", getHandlerName());
            return nextHandler.handle(request);
        } else {
            log.debug("Handler {} cannot handle request and no next handler available", getHandlerName());
            return handleDefault(request);
        }
    }
    
    /**
     * Performs the actual handling logic
     * To be implemented by concrete handlers
     */
    protected abstract R doHandle(T request);
    
    /**
     * Handles the request when no handler in the chain can process it
     * Default implementation returns null, can be overridden
     */
    protected R handleDefault(T request) {
        return null;
    }
}

