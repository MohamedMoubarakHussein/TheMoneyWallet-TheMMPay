package com.walletservice.service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
public class eventListener {
    
  
 
    /* 
    @KafkaListener(topics = "auth-signup-event", groupId = "user-service")
    public void handleSignupEvent(AuthEvent eventz) {
       
    }*/
}