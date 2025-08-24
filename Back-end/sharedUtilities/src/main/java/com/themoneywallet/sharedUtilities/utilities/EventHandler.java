package com.themoneywallet.sharedUtilities.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.sharedUtilities.dto.event.Event;
import com.themoneywallet.sharedUtilities.enums.EventType;
import com.themoneywallet.sharedUtilities.enums.ResponseKey;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class EventHandler {
    private final ObjectMapper objectMapper;

    public Event makeEvent(
        EventType eventType,
        String userId,
        Map<String, Map<String, String>> additionalData
    ) {
        // Use ThreadLocalRandom for better performance than UUID.randomUUID()
        String eventId = Long.toHexString(ThreadLocalRandom.current().nextLong()) + 
                        Long.toHexString(System.nanoTime());
        
        return Event.builder()
            .eventId(eventId)
            .eventType(eventType)
            .timestamp(LocalDateTime.now())
            .userId(userId)
            .additionalData(additionalData)
            .build();
    }


    public Event makeEvent(EventType eventType,String userId,String key,Object data) throws JsonProcessingException{
        Map<String, Map<String, String>> additionalData = new HashMap<>();
        
        additionalData.put(ResponseKey.DATA.toString(), Map.of(key ,  this.objectMapper.writeValueAsString(data)));
        additionalData.put(ResponseKey.INFO.name(), Map.of("structure" , "This object stored in the following key ("+key+") and has the following object" + data));
        return Event.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType(eventType)
            .timestamp(LocalDateTime.now())
            .userId(userId)
            .additionalData(additionalData)
            .build();
    }
}
