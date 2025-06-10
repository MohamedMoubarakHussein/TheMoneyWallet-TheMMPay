package com.themoneywallet.dashboardservice.utilities;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.themoneywallet.dashboardservice.event.Event;
import com.themoneywallet.dashboardservice.event.EventType;

@Component
public class EventHandler {

    public Event makeEvent(
        EventType eventType,
        String userId,
        Map<String, Map<String, String>> additionalData
    ) {
        return Event.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType(eventType)
            .timestamp(LocalDateTime.now())
            .userId(userId)
            .additionalData(additionalData)
            .build();
    }
}
