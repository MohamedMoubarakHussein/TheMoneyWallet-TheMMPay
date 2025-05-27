package com.themoneywallet.usermanagmentservice.utilite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class HttpHelper {
    /*
    @Autowired
    private WebClient webClient;

    public ResponseEntity<String> sendDataToUserMangmentService(String token) {
        String url = "http://localhost:8080/user/getidbytoken";
        
        // Send the request and handle response asynchronously
       ResponseEntity<String> res =   webClient.get()
                .uri(url)
                .header("Authorization", token)
                .retrieve()
                .toEntity(String.class)
                .block();

        return res;
               
    }
         */
}