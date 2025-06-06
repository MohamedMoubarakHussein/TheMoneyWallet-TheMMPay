package com.themoneywallet.historyservice.utilites;


import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.themoneywallet.historyservice.dto.response.UnifiedResponse;
import com.themoneywallet.historyservice.entity.fixed.ResponseKey;

/*
 * This class just showing and building
 *  the validation errors array with each field has a key in the map
 *  and the value has a list of all errors that related to that field
 *
 */
@Component
@RequiredArgsConstructor
public class ValidtionRequestHandler {

    private final UnifidResponseHandler uHandler;

    public UnifiedResponse handle(BindingResult result) {
        Map<String, String> data = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            data.put(error.getField(), error.getDefaultMessage());
        }

        return this.uHandler.makResponse(
                true,
                this.uHandler.makeRespoData(ResponseKey.ERROR, data),
                true,
                "AUVD11001"
            );
    }
}
