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

    @KafkaListener(topics = "auth-signup-event", groupId = "user-service")
    public void handleSignupEvent(Event eventz)
        throws JsonMappingException, JsonProcessingException {
        // switch event based on the eventz type
        log.info(eventz.toString());
        Map<String, String> event = eventz
            .getAdditionalData()
            .get(ResponseKey.DATA.toString());

        User profile = objectMapper.readValue(
            event.get("usreData"),
            User.class
        );
        profile.setUserRole(UserRole.ROLE_USER);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        this.profileRepository.save(profile);
        try {
            Event event2 = eventHandler.makeEvent(
                EventType.USER_PROFILE_CREATED,
                UUID.randomUUID().toString(),
                this.uHandler.makeRespoData(
                        ResponseKey.DATA,
                        Map.of(
                            "profile",
                            this.objectMapper.writeValueAsString(profile)
                        )
                    )
            );

            this.eventProducer.publishSignUpEvent(event2);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
