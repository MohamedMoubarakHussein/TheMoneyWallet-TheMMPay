package com.themoneywallet.authenticationservice.utilities;

import com.themoneywallet.authenticationservice.event.UserCreationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class HttpHelper {

    private final RestTemplate restTemplate;

    public ResponseEntity<String> sendDataToUserMangmentService(
        UserCreationEvent info
    ) {
        String url = "http://localhost:8080/user/signup";
        return restTemplate.postForEntity(url, info, String.class);
        // Send the request and handle response asynchronously

    }
}
