package com.themoneywallet.notificationservice.service.shared;

import com.themoneywallet.notificationservice.event.Event;
import com.themoneywallet.notificationservice.event.EventType;
import com.themoneywallet.notificationservice.service.NotificationService;
import com.themoneywallet.notificationservice.utilites.EventHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
public class eventListener {

    private final EventHandler eventHandler;

    private final NotificationService notificationService;

    @KafkaListener(topics = {"auth-user-signup"}, groupId = "notification-service")
    public void eventHandlers(Event event) {
        switch(event.getEventType()){
            case  EventType.AUTH_USER_SIGN_UP:                
                this.notificationService.sendEmailVerfication(event);
                break;
            case EventType.AUTH_USER_LOGIN_SUCCESSED:
                break;
            default:
            break;
        }
        
    }
}
