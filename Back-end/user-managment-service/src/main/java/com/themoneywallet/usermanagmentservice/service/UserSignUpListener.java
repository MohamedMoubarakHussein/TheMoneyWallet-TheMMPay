package com.themoneywallet.usermanagmentservice.service;

import java.time.Instant;
import java.util.Date;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.themoneywallet.usermanagmentservice.entity.Role;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.entity.UserPreferences;
import com.themoneywallet.usermanagmentservice.event.UserSingUpEvent;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;

import lombok.Data;

@Service
@Data
public class UserSignUpListener {
    
    private final UserRepository profileRepository;
    
 
    
    @KafkaListener(topics = "user-signup-events")
    public void handleSignupEvent(UserSingUpEvent event) {
        // Check if profile already exists (idempotency)
        if (profileRepository.existsById(event.getId())) {
            return;
        }
       
        User profile = new User();
        profile.setId(event.getId());
        profile.setUserName(event.getUserName());
        profile.setFirstName(event.getFirstName());
        profile.setLastName(event.getLastName());
        profile.setEmail(event.getEmail());
        profile.setPassword(event.getPassword());
        profile.setRole(Role.USER);
        profile.setPreferences(UserPreferences.builder().additionalPreferences(event.getPreferences()).build());
        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());
        
        profileRepository.save(profile);
    }
}