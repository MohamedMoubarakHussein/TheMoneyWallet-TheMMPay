package com.gatewayApi.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
/**
 * checking if the token vaild or not from auth service
 */
@Component
public class HttpService {

    @SuppressWarnings("null")
    public boolean isTokenValid(String token) {
        if(token == null)
            return false;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        final String jsonBody = "{\"token\": \"" + token + "\"}";
        ResponseEntity<Boolean> responseEntity;

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(jsonBody, headers);

        try {
            responseEntity = restTemplate.exchange(
                    "lb://authentication-service/auth/validate",
                    HttpMethod.POST,
                    entity,
                    Boolean.class);
        } catch (RestClientException e) {
            responseEntity = new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        return responseEntity.getBody();
    }
}
