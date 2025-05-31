package com.themoneywallet.notificationservice.utilites;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Component
@RequiredArgsConstructor
public class ValidationErrorMessageConverter {

    private final UnifidResponseHandler uResponseHandler;

    public String Convert(BindingResult result) {
        StringBuilder error = new StringBuilder("ValIdation errors:\n");
        for (FieldError fieldError : result.getFieldErrors()) {
            error.append(fieldError.getDefaultMessage()).append("\n");
        }

        return uResponseHandler
            .makResponse(
                true,
                Map.of("error", Map.of("message", error.toString())),
                true,
                "WA002"
            )
            .toString();
    }
}
