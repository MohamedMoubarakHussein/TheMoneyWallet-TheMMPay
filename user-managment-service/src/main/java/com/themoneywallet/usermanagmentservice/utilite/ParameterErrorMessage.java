package com.themoneywallet.usermanagmentservice.utilite;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ParameterErrorMessage {
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServeltRequestParameter(MissingServletRequestParameterException e){
        return new ResponseEntity<>("Required Parameter "+e.getParameterName()+" is not pressent", HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleExceptions(Exception ex){
        
        if(ex instanceof ResponseStatusException){
            ResponseStatusException rs = (ResponseStatusException) ex;

            return new ResponseEntity<>(ex.getMessage(), rs.getStatusCode());
        }

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
