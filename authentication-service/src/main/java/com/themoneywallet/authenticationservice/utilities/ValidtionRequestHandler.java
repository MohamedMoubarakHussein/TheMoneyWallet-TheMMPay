package com.themoneywallet.authenticationservice.utilities;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Component
public class ValidtionRequestHandler {
    
    public String handle(BindingResult result){
        StringBuilder ans = new StringBuilder("There are  validation errors in your request in the following fields:\n");
        for(FieldError error : result.getFieldErrors()){
            ans.append(error.getDefaultMessage()).append("\n");
        }
        return ans.toString();
    }
}
