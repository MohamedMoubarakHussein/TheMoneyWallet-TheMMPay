package com.themoneywallet.usermanagmentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.sharedUtilities.dto.event.Event;
import com.themoneywallet.sharedUtilities.utilities.EventHandler;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;
import com.themoneywallet.sharedUtilities.enums.EventType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
public class eventListener {

    private final UserRepository profileRepository;
    private final ObjectMapper objectMapper;
    private final EventProducer eventProducer;
    private final EventHandler eventHandler;
    private final UnifidResponseHandler uHandler;
    private final UserService userService;

    @KafkaListener(topics = "auth-user-signup", groupId = "user-service")
    public void handleEvents(Event event){
            switch (event.getEventType()) {
                case EventType.AUTH_USER_SIGN_UP:
                    this.userService.createProfile(event);
                    break;
            
                default:
                    break;
            }
    }


}
