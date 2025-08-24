package com.themoneywallet.sharedUtilities.ports;

/**
 * Simple cache abstraction used to decouple services from concrete cache technologies (e.g., Redis).
 */
public interface CachePort {

    void put(String key, String value);

    String get(String key);

    void delete(String key);
}
