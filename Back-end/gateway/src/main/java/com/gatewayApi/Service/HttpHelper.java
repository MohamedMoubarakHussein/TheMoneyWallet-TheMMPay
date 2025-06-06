package com.gatewayApi.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
/**
 * checking if the token vaild or not from auth service
 */
@Component
@Slf4j
public class HttpHelper {

    public ResponseEntity<String> getRefToken(String token) {
  
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
   
        ResponseEntity<String> responseEntity;

        headers.add("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<String>(headers);

        try {
            responseEntity = restTemplate.exchange(
                    "http://192.168.1.9:8099/auth/refreshtoken2",
                    HttpMethod.POST,
                    entity,
                    String.class);
        } catch (RestClientException e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return null;
        }
        return responseEntity;
    }
}
