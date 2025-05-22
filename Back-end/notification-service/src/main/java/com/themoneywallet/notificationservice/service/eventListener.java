package com.themoneywallet.notificationservice.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.notificationservice.entity.Notification;
import com.themoneywallet.notificationservice.event.Event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
@RequiredArgsConstructor
public class eventListener {
    
    private final  ObjectMapper objectMapper;
    private final EventProducer eventProducer; 
    private final  NotificationService notificationService;
    

    @KafkaListener(topics = {"transaction-events" ,"wallet-events" , "user-events"}, groupId = "notification-service")
    public void handleEvents(Event event) {
        Notification request;
    //    notificationService.processNotification(request);
    }
    
  
}