package com.walletservice.event;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthEvent {
    private String eventId;
    private String userId;
    private AuthEventType eventType;
    private LocalDateTime timestamp;
    private Map<String, Object> additionalData;
    

}