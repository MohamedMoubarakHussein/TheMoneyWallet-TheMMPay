package com.themoneywallet.usermanagmentservice.utilite;



import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


import lombok.RequiredArgsConstructor;
/*
 * This class just showing and building 
 *  the validation errors array with each field has a key in the map
 *  and the value has a list of all errors that related to that field
 *  
 */
/* 
@Component
@RequiredArgsConstructor
public class ValidtionRequestHandler {
    
    public UnifiedResponse handle(BindingResult result , UnifiedResponse unifiedResponse){
        Map<String,String> data = new HashMap<>();
        for(FieldError error : result.getFieldErrors()){
            data.put( error.getField(), error.getDefaultMessage());
        }

        unifiedResponse.setHaveData(true);
        unifiedResponse.setHaveError(true);
        unifiedResponse.setData(data);
        unifiedResponse.setStatusInternalCode("Val001");
        return unifiedResponse;
    }
}

*/