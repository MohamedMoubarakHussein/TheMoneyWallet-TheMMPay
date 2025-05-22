package com.themoneywallet.transcationservice.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.transcationservice.event.Event;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
public class eventListener {
    
    
    private final  ObjectMapper objectMapper;
    private final EventProducer eventProducer;
 
    
    @KafkaListener(topics = "auth-signup-event", groupId = "user-service")
    public void handleSignupEvent(Event eventz) {
      
        
    }
}