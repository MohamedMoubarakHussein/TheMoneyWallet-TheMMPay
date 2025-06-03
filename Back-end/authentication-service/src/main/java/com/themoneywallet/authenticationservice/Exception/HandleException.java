package com.themoneywallet.authenticationservice.Exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HandleException {

    @Profile("dev")
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessing(
        JsonProcessingException ex
    ) {
        return new ResponseEntity<>(
            "error has occured :\n" +
            ex.getMessage() +
            "Internal or viewd by admin" +
            ex.getCause() +
            " location " +
            ex.getLocation(),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>(
            "error has occured :\n" + ex.getMessage(),
            HttpStatus.BAD_REQUEST
        );
    }
}
