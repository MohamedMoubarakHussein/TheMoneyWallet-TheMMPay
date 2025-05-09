package com.themoneywallet.authenticationservice.event;
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
    
    public enum AuthEventType {
        LOGIN_SUCCESS, LOGIN_FAILED, LOGOUT, PASSWORD_CHANGED, 
        ACCOUNT_LOCKED, ACCOUNT_UNLOCKED, TOKEN_EXPIRED ,SIGN_UP,
        EMAIL_VERIFIY , EMAIL_NOTIFCATION , FORGET_PASSWORD

    }
}