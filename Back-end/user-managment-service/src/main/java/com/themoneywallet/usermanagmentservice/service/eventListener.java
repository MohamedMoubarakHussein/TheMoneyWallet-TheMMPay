package com.themoneywallet.usermanagmentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.entity.fixed.ResponseKey;
import com.themoneywallet.usermanagmentservice.entity.fixed.UserRole;
import com.themoneywallet.usermanagmentservice.event.Event;
import com.themoneywallet.usermanagmentservice.event.EventType;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;
import com.themoneywallet.usermanagmentservice.utilite.EventHandler;
import com.themoneywallet.usermanagmentservice.utilite.UnifidResponseHandler;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
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
                case EventType.AUTH_USER_LOGIN_SUCCESSED:
                    this.userService.userLogin(event);
                    break;
                case EventType.AUTH_USER_SIGN_UP:
                    log.info("recived event  "+ event);
                    this.userService.handleSignup(event);
                    break;
            
                default:
                    break;
            }
    }


}
