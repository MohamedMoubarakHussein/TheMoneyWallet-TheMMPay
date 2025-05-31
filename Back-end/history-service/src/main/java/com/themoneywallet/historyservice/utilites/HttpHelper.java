package com.themoneywallet.historyservice.utilites;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class HttpHelper {

    public ResponseEntity<String> sendDataToUserMangmentService(
        String token,
        String refToken
    ) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<String> responseEntity;

        headers.add("Authorization", "Bearer " + token);
        headers.add(HttpHeaders.COOKIE, "refreshToken=" + refToken);

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        log.info(token);
        try {
            responseEntity = restTemplate.exchange(
                "http://localhost:8080/user/getidbytoken",
                HttpMethod.GET,
                entity,
                String.class
            );
        } catch (RestClientException e) {
            responseEntity = new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
        log.info(
            "xxx " +
            responseEntity.getBody() +
            " xxx " +
            responseEntity.getStatusCode()
        );
        return responseEntity;
    }
}
