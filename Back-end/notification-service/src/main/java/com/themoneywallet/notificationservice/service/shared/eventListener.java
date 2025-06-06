package com.themoneywallet.notificationservice.service.shared;

import com.themoneywallet.notificationservice.event.Event;
import com.themoneywallet.notificationservice.service.NotificationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
public class eventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "USER_PROFILE_CREATED", groupId = "notification")
    public void consumeNotificationEvent(Event event) {
        notificationService.processEvent(event);
    }
}
