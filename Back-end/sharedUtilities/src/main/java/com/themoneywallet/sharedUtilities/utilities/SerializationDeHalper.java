package com.themoneywallet.sharedUtilities.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.sharedUtilities.enums.FixedInternalValues;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SerializationDeHalper {

    private final ObjectMapper objectMapper;

    public <T> String serailization(T obj) {
        try {
            return this.objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return FixedInternalValues.ERROR_HAS_OCCURED.toString();
        }
    }

    /* 0 -> no error  state 1 -> error
     */
    public <T> Pair<T, Integer> deserailization(
        String json,
        Class<T> target
    ) {
        try {
            return new Pair<T, Integer>(
                this.objectMapper.readValue(json, target),
                0
            );
        } catch (JsonProcessingException e) {
          
                return new Pair<T, Integer>(null, 1);
        }
    }
}
