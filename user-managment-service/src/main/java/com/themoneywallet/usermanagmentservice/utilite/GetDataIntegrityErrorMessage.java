package com.themoneywallet.usermanagmentservice.utilite;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class GetDataIntegrityErrorMessage {
    
    public String getMessage(DataIntegrityViolationException e){
        String err = e.getMessage();
        if(err.contains("UK_email")){
            return "Email address is already in use. please choose a different one.";
        }else if(err.contains("UK_userName")){
            return "userName address is already in use. please choose a different one.";
        }
        return "Error has happend in database";
    }
}
