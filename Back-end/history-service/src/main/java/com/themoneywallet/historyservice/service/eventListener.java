package com.themoneywallet.historyservice.service;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.historyservice.event.Event;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
public class eventListener {
    
    private final  ObjectMapper objectMapper;
    private final EventProducer eventProducer;
 
    
    @KafkaListener(topics = {"auth-signup-event"}, groupId = "history-service")
    public void handleSignupEvent(Event event) {

        
     
    }
}