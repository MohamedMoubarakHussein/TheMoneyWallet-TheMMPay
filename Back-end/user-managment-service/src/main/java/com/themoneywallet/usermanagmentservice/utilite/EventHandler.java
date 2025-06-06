package com.themoneywallet.usermanagmentservice.utilite;

import com.themoneywallet.usermanagmentservice.event.Event;
import com.themoneywallet.usermanagmentservice.event.EventType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

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
