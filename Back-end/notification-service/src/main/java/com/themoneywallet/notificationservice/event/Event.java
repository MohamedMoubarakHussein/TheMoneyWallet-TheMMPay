package com.themoneywallet.notificationservice.event;
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
public class Event {
    private String eventId;
    private String userId;
    private EventType eventType;
    private LocalDateTime timestamp;
    private Map<String, Object> additionalData;
    

}