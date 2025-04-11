package com.themoneywallet.authenticationservice.utilities;



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
@Component
@RequiredArgsConstructor
public class ValidtionRequestHandler {
    private final  ResponseHandller myResponse;
    public void handle(BindingResult result){

        for(FieldError error : result.getFieldErrors()){
            myResponse.addingErrorResponse(error.getField(), error.getDefaultMessage());
         
        }

    }
}
