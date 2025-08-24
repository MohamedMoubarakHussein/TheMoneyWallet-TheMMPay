package com.themoneywallet.sharedUtilities.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnifiedResponseHandler {

    private final ObjectMapper objectMapper;

    public ResponseEntity<UnifiedResponse> generateFailedResponse(String key, Object message, String code, String deserializationInfo, HttpStatus httpStatusCode) {
        return new UnifiedResponseBuilder(objectMapper, httpStatusCode)
                .withError(key, message, code, deserializationInfo)
                .build();
    }

    public ResponseEntity<UnifiedResponse> generateSuccessResponse(String key, Object message, HttpStatus status) {
        return new UnifiedResponseBuilder(objectMapper, status)
                .withData(key, message)
                .build();
    }

    public ResponseEntity<UnifiedResponse> generateSuccessResponseNoBody(String key, HttpStatus status) {
        return new UnifiedResponseBuilder(objectMapper, status)
                .withSuccessNoBody(key)
                .build();
    }

    public ResponseEntity<UnifiedResponse> generateSuccessResponseNoBody(String key, HttpStatus status, HttpHeaders headers) {
        return ResponseEntity.status(status)
                .headers(headers)
                .body(new UnifiedResponseBuilder(objectMapper, status)
                        .withSuccessNoBody(key)
                        .build().getBody());
    }

    public ResponseEntity<UnifiedResponse> generateSuccessResponse(String key, Object message, HttpStatus status, HttpHeaders headers) {
        return ResponseEntity.status(status)
                .headers(headers)
                .body(new UnifiedResponseBuilder(objectMapper, status)
                        .withData(key, message)
                        .build().getBody());
    }
}