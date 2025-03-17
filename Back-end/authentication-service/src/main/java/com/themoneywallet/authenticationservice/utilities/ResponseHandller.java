package com.themoneywallet.authenticationservice.utilities;

import java.util.ArrayList;


import org.springframework.stereotype.Component;
import com.themoneywallet.authenticationservice.dto.response.ErrorsResponse;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Data
public class ResponseHandller {
    private final ErrorsResponse response;
    
    public void clearErrors(){
        response.getErrorsMap().clear();
        response.getResponsMap().clear();
    }

    public void addingError(String fieldName ,String error ){
        response.getErrorsMap().computeIfAbsent(fieldName, ls -> new ArrayList<>()).add(error);
        response.getResponsMap().put("errors", response.getErrorsMap());

    }

}
