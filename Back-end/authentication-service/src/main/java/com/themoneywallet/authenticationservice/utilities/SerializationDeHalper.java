package com.themoneywallet.authenticationservice.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.authenticationservice.entity.fixed.FixedInternalValues;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
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

    /* 0 -> no error  state 1 -> comes from internal   2-> user
     */
    public <T> Pair<T, Integer> deserailization(
        String json,
        Class<T> target,
        int state
    ) {
        try {
            return new Pair<T, Integer>(
                this.objectMapper.readValue(json, target),
                0
            );
        } catch (JsonProcessingException e) {
            if (state == 1) {
                return new Pair<T, Integer>(null, 1);
            } else {
                return new Pair<T, Integer>(null, 2);
            }
        }
    }
}
