package com.walletservice.utilites;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.walletservice.event.Event;
import com.walletservice.event.EventType;

@Component
public class EventHandler {



    public Event makeEvent(EventType eventType,String userId,
                                Map<String, Object> additionalData)
    {
        return  Event.builder().eventId(UUID.randomUUID().toString())
                               .eventType(eventType)
                               .timestamp(LocalDateTime.now())
                               .userId(userId)
                               .additionalData(additionalData).build();
    }

}
