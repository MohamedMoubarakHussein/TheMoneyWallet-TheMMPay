package com.themoneywallet.usermanagmentservice.service;

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
import com.themoneywallet.usermanagmentservice.dto.request.UserSingUpEvent;
import com.themoneywallet.usermanagmentservice.entity.Role;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.entity.UserPreferences;
import com.themoneywallet.usermanagmentservice.event.Event;
import com.themoneywallet.usermanagmentservice.event.EventType;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
public class eventListener {
    
    private final UserRepository profileRepository;
    private final  ObjectMapper objectMapper;
    private final EventProducer eventProducer;
 
    
    @KafkaListener(topics = "auth-signup-event", groupId = "user-service")
    public void handleSignupEvent(Event eventz) {
       // switch event based on the eventz type
        log.info(eventz.toString());
        Object event =  eventz.getAdditionalData().get("userData");
        HashMap<String , Object>  user = this.objectMapper.convertValue(event,  new TypeReference<HashMap<String, Object>>() {});
        HashMap<String , String>   prefernce= (HashMap<String, String>) user.remove("preferences");
        user.remove("preferences");
        UserSingUpEvent  userSingUpDetails=  objectMapper.convertValue(user, UserSingUpEvent.class);
       
        UserPreferences userPreferences =  objectMapper.convertValue(prefernce, UserPreferences.class);
        // Check if profile already exists (idempotency)
        if (profileRepository.existsById(userSingUpDetails.getId())) {
            return;
        }
       
        User profile = new User();
        profile.setId(userSingUpDetails.getId());
        profile.setUserName(userSingUpDetails.getUserName());
        profile.setFirstName(userSingUpDetails.getFirstName());
        profile.setLastName(userSingUpDetails.getLastName());
        profile.setEmail(userSingUpDetails.getEmail());
        profile.setPassword(userSingUpDetails.getPassword());
        profile.setRole(Role.USER);
        profile.setPreferences(userPreferences);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        
        this.profileRepository.save(profile);
        try {
            Event event2 = new Event();
            event2.setEventId(UUID.randomUUID().toString());
            event2.setEventType(EventType.USER_PROFILE_CREATED);
            event2.setUserId(String.valueOf(profile.getId()));
            event2.setTimestamp(LocalDateTime.now() );
            event2.setAdditionalData(Map.of("data", profile));
            this.eventProducer.publishSignUpEvent(event2);
        } catch (Exception e) {
           log.info(e.getMessage());
        }
        
    }
}