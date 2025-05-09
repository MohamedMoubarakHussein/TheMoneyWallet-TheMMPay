package com.themoneywallet.usermanagmentservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.themoneywallet.usermanagmentservice.event.UserSingUpEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    private final KafkaTemplate<String, UserSingUpEvent> kafkaTemplate;
    
    public void publishSignUpEvent(UserSingUpEvent event) {
        kafkaTemplate.send("user-signup-events", event);
        log.info("Published user event: {}", event);
    }
    
}