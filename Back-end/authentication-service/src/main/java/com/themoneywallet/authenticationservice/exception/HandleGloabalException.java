package com.themoneywallet.authenticationservice.exception;

import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class HandleGloabalException {
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    private final UnifidResponseHandler uHandler;
   
    @ExceptionHandler(Exception.class)
    public ResponseEntity<UnifiedResponse> handleException(Exception ex) {
        if(activeProfile.equals("dev")){
           return this.uHandler.generateFailedResponse("error", "error has occured :\n" + ex.getMessage(), "AUGE0002", "string", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return  this.uHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUGE0002. We apologize for the inconvenience." , "AUGE0002", "string", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
