package com.themoneywallet.notificationservice.utilites;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.notificationservice.dto.response.UnifiedResponse;
import com.themoneywallet.notificationservice.entity.fixed.ResponseKey;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnifidResponseHandler {

    private final ObjectMapper objectMapper;

    public UnifiedResponse makResponse(
        boolean haveData,
        Map<String, Map<String, String>> data,
        boolean haveError,
        String statusInternalCode
    ) {
        return  UnifiedResponse
            .builder()
            .timeStamp(LocalDateTime.now())
            .haveData(haveData)
            .data(data)
            .haveError(haveError)
            .statusInternalCode(statusInternalCode)
            .build();
    }



     public ResponseEntity<UnifiedResponse> generateFailedResponse(String key ,String message , String code){
                    Map<String, Map<String, String>> data = Map.of(
                        ResponseKey.ERROR.toString(),
                        Map.of(key , message)

                         );
                    data.put(ResponseKey.INFO.toString(), Map.of("structure" ,String.format(
                            "Error has occured and you can retrive the data using key (%s) " , key
                    )));
            return ResponseEntity.badRequest()
            .body(
                this.makResponse(
                        true,
                        data,
                        true,
                        code
                    )
            ); 

        }


         public ResponseEntity<UnifiedResponse> generateSuccessResponse(String key ,Object message , HttpStatus status){
                    Map<String, Map<String, String>> data;
                    try {
                        data = Map.of(
                            ResponseKey.DATA.toString(),
                            Map.of(key , this.objectMapper.writeValueAsString(message))

                             );
                    } catch (JsonProcessingException e) {
                        return this.generateFailedResponse("error", "Contact the admin of the site with the following code #NOMP00002", "NOMP00002");
                    }
                    data.put(ResponseKey.INFO.toString(), Map.of("structure" ,String.format(
                            " you can retrive the data using the key (%s)  the  structure looks like that  (%s)" , key , message.toString()
                    )));
            return ResponseEntity.status(status)
            .body(
                this.makResponse(
                        true,
                        data,
                        false,
                        null
                    )
            ); 

        }
}
