package com.walletservice.utilites;

import java.time.Instant;

import java.util.Map;


import org.springframework.stereotype.Component;

import com.walletservice.dto.response.UnifiedResponse;


@Component

public class UnifidResponseHandler {

    
    public UnifiedResponse makResponse(
        boolean haveData ,
        Map<String , String> data,
        boolean haveError , 
        String statusInternalCode
    ){

        return new UnifiedResponse().builder()
                                    .timeStamp(Instant.now())
                                    .haveData(haveData)
                                    .data(data)
                                    .haveError(haveError)
                                    .statusInternalCode(statusInternalCode)
                                    .build();


       



    

    }
}
