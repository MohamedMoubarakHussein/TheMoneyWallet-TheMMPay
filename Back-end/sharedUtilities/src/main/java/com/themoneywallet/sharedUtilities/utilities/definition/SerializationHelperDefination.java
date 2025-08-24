package com.themoneywallet.sharedUtilities.utilities.definition;

import java.util.Optional;

public interface SerializationHelperDefination {
    <T> Optional<String> serialize(T obj);
    <T> Optional<T> deserialize(String json, Class<T> targetClass);
}