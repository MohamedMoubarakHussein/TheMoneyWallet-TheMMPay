package com.themoneywallet.usermanagmentservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.themoneywallet.usermanagmentservice.dto.request.UserSingUpEvent;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.event.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    private final KafkaTemplate<String, Event> kafkaTemplate;
    
    public void publishProfileCreatedEvent(Event event) {
        kafkaTemplate.send("user-profile-created-event", event.getEventId(),event);
        log.info("Published user event: {}", event);
    }
    
}