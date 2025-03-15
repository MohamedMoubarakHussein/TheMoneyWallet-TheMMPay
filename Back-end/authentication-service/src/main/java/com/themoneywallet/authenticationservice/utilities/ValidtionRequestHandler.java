package com.themoneywallet.authenticationservice.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Component
public class ValidtionRequestHandler {
    
    public Map<String, List<String>> handle(BindingResult result, Map<String , List<String>> errorsMap){
        StringBuilder ans = new StringBuilder("There are  validation errors in your request in the following fields:\n");
        for(FieldError error : result.getFieldErrors()){
            errorsMap.computeIfAbsent(error.getField(), ls -> new ArrayList<>())
                .add(error.getDefaultMessage());
            
        }
        return errorsMap;
    }
}
