package com.themoneywallet.sharedUtilities.utilities;

import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.enums.ResponseKey;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


@RequiredArgsConstructor
public class ValidtionRequestHandler {
    private final UnifidResponseHandler uHandler;

    public UnifiedResponse handle(BindingResult result) {
        Map<String, String> data = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            data.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Map<String, String>> mp = this.uHandler.makeRespoData(ResponseKey.ERROR, data);
        mp.put(ResponseKey.INFO.toString(), Map.of("structure" , "this is a message error so the structure is like  key(Name of the field that cause the error) -> value(The error details)"));
        return this.uHandler.makResponse(true,mp,true,"AUVD1001");
    }
}
