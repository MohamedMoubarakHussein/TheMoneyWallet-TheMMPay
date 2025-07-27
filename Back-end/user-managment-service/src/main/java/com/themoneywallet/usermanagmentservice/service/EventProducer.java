package com.themoneywallet.usermanagmentservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.themoneywallet.sharedUtilities.dto.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    private final KafkaTemplate<String, Event> kafkaTemplate;
    
    public void publishProfileDeleted(Event event) {
        kafkaTemplate.send("user-profile-delete-event", event.getEventId(),event);
        log.info("Published user event: {}", event);
    }
    public void publishProfileUpdated(Event event) {
        kafkaTemplate.send("user-profile-update-event", event.getEventId(),event);
        log.info("Published user event: {}", event);
    }
    
}