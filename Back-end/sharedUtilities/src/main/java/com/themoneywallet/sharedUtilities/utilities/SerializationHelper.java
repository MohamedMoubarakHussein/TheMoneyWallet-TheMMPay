package com.themoneywallet.sharedUtilities.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.sharedUtilities.utilities.definition.SerializationHelperDefination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class SerializationHelper implements SerializationHelperDefination {

    private final ObjectMapper objectMapper;

    @Override
    public <T> Optional<String> serialize(T obj) {
        if (obj == null) {
            return Optional.empty();
        }
        
        try {
            String result = objectMapper.writeValueAsString(obj);
            return Optional.of(result);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> deserialize(String json, Class<T> targetClass) {
        if (json == null || json.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            T result = objectMapper.readValue(json, targetClass);
            return Optional.of(result);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
