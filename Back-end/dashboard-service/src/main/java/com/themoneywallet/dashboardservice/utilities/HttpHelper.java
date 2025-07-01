package com.themoneywallet.dashboardservice.utilities;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class HttpHelper {

    private final RestTemplate restTemplate;

    public ResponseEntity<String> getIdFromTokenFromUserManagmentService(String token)
     {
        String url = "http://localhost:8080/user/getidbytoken";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                String.class
        );

    }
    
}
