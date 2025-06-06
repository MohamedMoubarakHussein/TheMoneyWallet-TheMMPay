package com.themoneywallet.notificationservice.service;

import com.themoneywallet.notificationservice.entity.Notification;
import com.themoneywallet.notificationservice.event.Event;
import com.themoneywallet.notificationservice.repository.NotificationRepository;
import com.themoneywallet.notificationservice.utilites.UnifidResponseHandler;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationTemplateResolver templateResolver;
    private final UnifidResponseHandler uResponseHandler;

    public void processEvent(Event event) {
        Notification notification = createNotification(event);
        repository.save(notification);
        log.info("notification" + notification);
        // Future extension: Trigger real-time push here
    }

    private Notification createNotification(Event event) {
        return Notification.builder()
            .userId(event.getUserId())
            .eventType(event.getEventType())
            .message(templateResolver.resolveTemplate(event))
            .metadata(event.getAdditionalData())
            .build();
    }

    public ResponseEntity<String> getAllUserNotifications(String userId) {
        return ResponseEntity.badRequest()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of(
                            "data",
                            Map.of(
                                "notification",
                                repository.findByUserId(userId).toString()
                            )
                        ),
                        false,
                        null
                    ).toString()
            );
    }

    public ResponseEntity<String> getUnreadNotifications(String userId) {
        return ResponseEntity.badRequest()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of(
                            "data",
                            Map.of(
                                "notification",
                                repository
                                    .findByUserIdAndReadFalse(userId)
                                    .toString()
                            )
                        ),
                        false,
                        null
                    ).toString()
            );
    }

    public void markNotificationRead(String id) {
        repository
            .findById(id)
            .ifPresent(notification -> {
                notification.setRead(true);
                repository.save(notification);
            });
    }
}
