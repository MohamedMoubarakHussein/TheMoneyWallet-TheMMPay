package com.themoneywallet.sharedUtilities.patterns.factory;

/**
 * Generic factory interface following Factory Pattern
 * Provides object creation abstraction
 */
public interface ServiceFactory<T, K> {
    
    /**
     * Creates an instance based on the provided key
     * @param key the key identifying which instance to create
     * @return the created instance
     */
    T create(K key);
    
    /**
     * Checks if the factory supports the given key
     * @param key the key to check
     * @return true if supported, false otherwise
     */
    boolean supports(K key);
    
    /**
     * Returns the factory name for identification
     * @return factory name
     */
    String getFactoryName();
}

