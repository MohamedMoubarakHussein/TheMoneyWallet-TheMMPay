package com.themoneywallet.authenticationservice.utilities;

import java.util.ArrayList;

import org.springframework.stereotype.Component;
import com.themoneywallet.authenticationservice.dto.response.UnifiedResponse;

import jakarta.inject.Provider;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Data
public class ResponseHandller {
    private final Provider<UnifiedResponse> response;
    
    public void clearResponse(){
        response.get().getNeastedResponseMap().clear();
        response.get().getResponsMap().clear();
    }

    public   UnifiedResponse getCurrentResponse(){
        return this.response.get();
    }

    public void addingResponse(String fieldName ,String value ){
        response.get().getResponsMap().computeIfAbsent(fieldName, ls -> value);

    }

    public void addingErrorResponse(String fieldName ,String error ){
        response.get().getNeastedResponseMap().computeIfAbsent(fieldName, ls -> new ArrayList<>()).add(error);
        response.get().getResponsMap().put("errors", response.get().getNeastedResponseMap());

    }

  

}
