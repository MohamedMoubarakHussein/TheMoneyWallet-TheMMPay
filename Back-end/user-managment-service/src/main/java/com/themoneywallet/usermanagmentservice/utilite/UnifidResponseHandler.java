package com.themoneywallet.usermanagmentservice.utilite;

import java.time.Instant;

import java.util.Map;


import org.springframework.stereotype.Component;

import com.themoneywallet.usermanagmentservice.dto.response.UnifiedResponse;




@Component

public class UnifidResponseHandler {

    
    public UnifiedResponse makResponse(boolean haveData ,
                                        Map<String , String> data,
                                        boolean haveError , 
                                        String statusInternalCode)
    {
        return  UnifiedResponse.builder()
                                .timeStamp(Instant.now())
                                .haveData(haveData)
                                .data(data)
                                .haveError(haveError)
                                .statusInternalCode(statusInternalCode)
                                .build();
    }
}
