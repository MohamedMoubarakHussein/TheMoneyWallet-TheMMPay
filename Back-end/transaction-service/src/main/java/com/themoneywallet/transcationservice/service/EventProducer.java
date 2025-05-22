package com.themoneywallet.transcationservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.themoneywallet.transcationservice.event.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    private final KafkaTemplate<String, Event> kafkaTemplate;
    
    public void publishSignUpEvent(Event event) {
        kafkaTemplate.send("user-signup-event", event.getEventId(),event);
        log.info("Published user event: {}", event);
    }
    
}