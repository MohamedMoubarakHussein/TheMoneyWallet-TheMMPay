package com.themoneywallet.authenticationservice.service.implementation;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.themoneywallet.authenticationservice.event.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    private final KafkaTemplate<String, Event> kafkaTemplate;
    
    public void publishSignUpEvent(Event event) {
        kafkaTemplate.send("auth-user-signup", event);
        log.info("Published auth event: {}", event);
    }
    
    public void publishSigninEvent(Event event) {
        kafkaTemplate.send("auth-user-login-sucessed-events", event);
        log.info("Published login event: {}", event);
    }

    public void publishForgotPasswordEvent(Event event) {
        kafkaTemplate.send("auth-forgotpassword-events", event.getUserId(), event);
        log.info("Published  event: {}", event);
    }

    public void publishVerfiyEvent(Event event) {
        kafkaTemplate.send("auth-verfiy-events", event.getUserId(), event);
        log.info("Published  event: {}", event);
    }
    public void publishLogOutEvent(Event event) {
        kafkaTemplate.send("auth-logout-events", event.getUserId(), event);
        log.info("Published  event: {}", event);
    }
}