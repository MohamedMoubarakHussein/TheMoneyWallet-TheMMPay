package com.themoneywallet.sharedUtilities.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.enums.ResponseKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationRequestHandler {

    private final ObjectMapper objectMapper;

   
    public UnifiedResponse handle(BindingResult result) {
        if (result == null || !result.hasErrors()) {
            log.warn("No validation errors found in BindingResult");
            return new UnifiedResponseBuilder(objectMapper).build(HttpStatus.OK).getBody();
        }

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
            log.debug("Validation error for field '{}': {}", error.getField(), error.getDefaultMessage());
        }

        return new UnifiedResponseBuilder(objectMapper)
                .withError(ResponseKey.ERROR.toString(), fieldErrors, "VAL001", "Map<String, String>")
                .build(HttpStatus.BAD_REQUEST).getBody();
    }
}