package com.walletservice.utilites;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import reactor.core.publisher.Mono;

@Service
public class HttpHelper {
    @Autowired
    private WebClient webClient;

    public ResponseEntity<String> sendDataToUserMangmentService(String token) {
        String url = "http://localhost:8080/user/getidbytoken";
        
        // Send the request and handle response asynchronously
       return  webClient.get()
                .uri(url)
                .header("Authorization", token)
                .retrieve()
                .toEntity(String.class)
                .block();
               
    }
}