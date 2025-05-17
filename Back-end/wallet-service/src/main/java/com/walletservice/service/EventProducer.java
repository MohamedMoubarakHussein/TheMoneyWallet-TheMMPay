package com.walletservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    /*
    private final KafkaTemplate<String, User> kafkaTemplate;
    
    public void publishSignUpEvent(User event) {
        kafkaTemplate.send("user-signup-event", event);
        log.info("Published user event: {}", event);
    }
     */
}