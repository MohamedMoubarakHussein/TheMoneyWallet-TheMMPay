package com.walletservice.utilites;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidationErrorMessageConverter {

    private final UnifidResponseHandler uResponseHandler;

        public String Convert(BindingResult result){
        StringBuilder error = new StringBuilder("ValIdation errors:\n");
          for(FieldError fieldError : result.getFieldErrors()){
            error.append(fieldError.getDefaultMessage()).append("\n");
          }  

         
          return  uResponseHandler.makResponse(true, Map.of("error" ,error.toString()), true, "WA002").toString();
    }
}
