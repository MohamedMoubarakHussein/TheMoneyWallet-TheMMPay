package com.themoneywallet.sharedUtilities.patterns.chain;

/**
 * Chain of Responsibility pattern interface
 * Allows requests to be passed along a chain of handlers
 */
public interface ChainHandler<T, R> {
    
    /**
     * Handles the request or passes it to the next handler
     * @param request the request to handle
     * @return the result of handling, or null if passed to next handler
     */
    R handle(T request);
    
    /**
     * Sets the next handler in the chain
     * @param nextHandler the next handler
     */
    void setNext(ChainHandler<T, R> nextHandler);
    
    /**
     * Checks if this handler can handle the request
     * @param request the request to check
     * @return true if this handler can handle the request
     */
    boolean canHandle(T request);
    
    /**
     * Returns the handler name for identification
     * @return handler name
     */
    String getHandlerName();
}

