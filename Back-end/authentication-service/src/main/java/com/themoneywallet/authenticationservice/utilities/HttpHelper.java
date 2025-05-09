package com.themoneywallet.authenticationservice.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.themoneywallet.authenticationservice.event.UserCreationEvent;

import reactor.core.publisher.Mono;

@Service
public class HttpHelper {
    @Autowired
    private WebClient webClient;

    public ResponseEntity<String> sendDataToUserMangmentService(UserCreationEvent info) {
        String url = "http://localhost:8080/user/signup";
 
        // Send the request and handle response asynchronously
       return  webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(info)
                .retrieve()

                .toEntity(String.class)
                .block();
               
    }
}