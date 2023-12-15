package com.themoneywallet.usermanagmentservice.utilite;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Component
public class ValidationErrorMessageConverter {
    
    public String Convert(BindingResult result){
        StringBuilder error = new StringBuilder("ValIdation errors:\n");
          for(FieldError fieldError : result.getFieldErrors()){
            error.append(fieldError.getDefaultMessage()).append("\n");
          }  
          return error.toString();
    }
}
