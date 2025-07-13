package com.walletservice.utilites;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletservice.dto.response.UnifiedResponse;
import com.walletservice.entity.fixed.ResponseKey;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
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

        public Map<String, Map<String, String>> makeRespoData(
        ResponseKey key,
        Map<String, String> insideMp
    ) {
        Map<String, Map<String, String>> data = new HashMap<>();
        data.computeIfAbsent(key.toString(), k -> new HashMap<>(insideMp));
        return data;
    }

     public ResponseEntity<UnifiedResponse> generateFailedResponse(String key ,String message , String code , String structureDescription){
                    Map<String, Map<String, String>> data = Map.of(
                        ResponseKey.ERROR.toString(),
                        Map.of(key , message)

                         );
                    data.put(ResponseKey.INFO.toString(), Map.of("structure" ,String.format(
                            "Error has occured and you can retrive the data using key (%s)  Deserlization to -> %s" , key , structureDescription
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
                    Map<String, Map<String, String>> data = new HashMap<>();
                    
                    try {
                        data.put(ResponseKey.DATA.toString(), Map.of(key , this.objectMapper.writeValueAsString(message)));
                    } catch (JsonProcessingException e) {
                        return this.generateFailedResponse("error", "Contact the admin of the site with the following code #NOMP00002", "NOMP00002" ," String");
                    }
                  
                data.put(ResponseKey.INFO.toString(), Map.of("structure" ,
                            " you can retrive the data using the key "+ key +"  Deserlization to ->  " + message.toString()
                    ));
   
                   
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
