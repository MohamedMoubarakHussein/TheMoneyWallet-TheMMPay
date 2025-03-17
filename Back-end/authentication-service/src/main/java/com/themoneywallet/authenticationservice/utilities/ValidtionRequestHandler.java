package com.themoneywallet.authenticationservice.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
/*
 * This class just showing and building 
 *  the validation errors array with each field has a key in the map
 *  and the value has a list of all errors that related to that field
 *  
 */
@Component
public class ValidtionRequestHandler {
    
    public Map<String, List<String>> handle(BindingResult result, Map<String , List<String>> errorsMap){
        for(FieldError error : result.getFieldErrors()){
            errorsMap.computeIfAbsent(error.getField(), ls -> new ArrayList<>())
                .add(error.getDefaultMessage());
        }
        return errorsMap;
    }
}
