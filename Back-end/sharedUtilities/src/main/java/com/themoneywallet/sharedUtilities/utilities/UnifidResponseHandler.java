package com.themoneywallet.sharedUtilities.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.enums.ResponseKey;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public class UnifidResponseHandler {

    private final ObjectMapper objectMapper;

    public UnifiedResponse makResponse(boolean haveData,Map<String, Map<String, String>> data,boolean haveError,String statusInternalCode) {
        return  UnifiedResponse.builder().timeStamp(LocalDateTime.now()).haveData(haveData).data(data).haveError(haveError).statusInternalCode(statusInternalCode).build();
    }

    public Map<String, Map<String, String>> makeRespoData(ResponseKey key,Map<String, String> insideMp) {
        Map<String, Map<String, String>> data = new HashMap<>();
        data.computeIfAbsent(key.toString(), k -> new HashMap<>(insideMp));
        return data;
    }
    

    public ResponseEntity<UnifiedResponse> generateFailedResponse(String key ,Object message , String code , String deSerializationInfo,HttpStatus httpStatusCode){
        Map<String, Map<String, String>> data = new HashMap<>();
        data.put(ResponseKey.INFO.toString(), Map.of("structure" ,String.format("Error has occured and you can retrive the data using key (%s)  Deserlization to -> %s" , key , deSerializationInfo)));
        try {
            data.put(ResponseKey.ERROR.toString(), Map.of(key , this.objectMapper.writeValueAsString(message)));
        } catch (JsonProcessingException e) {
            data.put(ResponseKey.ERROR.toString(), Map.of(key ,"Contact the admin of the site with the following code #NOMP00002"));
            return ResponseEntity.internalServerError().body(this.makResponse(true, data,true,"NOMP00002")); 
        }
        return ResponseEntity.status(httpStatusCode).body(this.makResponse( true,data,true,code)); 
    }


    public ResponseEntity<UnifiedResponse> generateSuccessResponse(String key ,Object message , HttpStatus status){
        Map<String, Map<String, String>> data = new HashMap<>();
        try {
            data.put(ResponseKey.DATA.toString(), Map.of(key , this.objectMapper.writeValueAsString(message)));
        } catch (JsonProcessingException e) {
            return this.generateFailedResponse("error", "Contact the admin of the site with the following code #NOMP00002", "NOMP00002" ," String" , HttpStatus.INTERNAL_SERVER_ERROR);
        }   
        data.put(ResponseKey.INFO.toString(), Map.of("structure" ," you can retrive the data using the key "+ key +"  Deserlization to ->  " + message.toString()));       
        return ResponseEntity.status(status).body(this.makResponse(true,data,false,null)); 
    }

    public ResponseEntity<UnifiedResponse> generateSuccessResponseNoBody(String key  , HttpStatus status ,HttpHeaders headers){
        Map<String, Map<String, String>> data = new HashMap<>();
        data.put(ResponseKey.DATA.toString(), Map.of(key , "no data"));
        data.put(ResponseKey.INFO.toString(), Map.of("structure" ," you can retrive the data using the key "+ key +"  Deserlization to ->  no data" ));
        return ResponseEntity.status(status).headers(headers).body(this.makResponse(false,null,false,null)); 
    }

    public ResponseEntity<UnifiedResponse> generateSuccessResponseNoBody(String key  , HttpStatus status ){
        Map<String, Map<String, String>> data = new HashMap<>();
        data.put(ResponseKey.DATA.toString(), Map.of(key , "No data"));
        data.put(ResponseKey.INFO.toString(), Map.of("structure" ," you can retrive the data using the key "+ key +"  Deserlization to ->   No data" ));
        return ResponseEntity.status(status).body(this.makResponse( false,  null, false,null)); 
    }


    public ResponseEntity<UnifiedResponse> generateSuccessResponse(String key ,Object message , HttpStatus status ,HttpHeaders headers){
        Map<String, Map<String, String>> data = new HashMap<>();
        try {
            data.put(ResponseKey.DATA.toString(), Map.of(key , this.objectMapper.writeValueAsString(message)));
        } catch (JsonProcessingException e) {
            return this.generateFailedResponse("error", "Contact the admin of the site with the following code #NOMP00002", "NOMP00002" ," String" , HttpStatus.INTERNAL_SERVER_ERROR);
        } 
        data.put(ResponseKey.INFO.toString(), Map.of("structure" , " you can retrive the data using the key "+ key +"  Deserlization to ->  " + message.toString()));
        return ResponseEntity.status(status).headers(headers)
        .body(this.makResponse(true,data,false,null)); 
    }
}
