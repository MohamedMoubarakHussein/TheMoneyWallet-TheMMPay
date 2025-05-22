package com.themoneywallet.notificationservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.themoneywallet.notificationservice.entity.Notification;
import com.themoneywallet.notificationservice.entity.NotificationStatus;
import com.themoneywallet.notificationservice.entity.NotificationTemplate;
import com.themoneywallet.notificationservice.entity.NotificationType;
import com.themoneywallet.notificationservice.entity.UserNotificationPreference;
import com.themoneywallet.notificationservice.repository.NotificationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    
   
    private final NotificationRepository notificationRepository;
    private final UserPreferenceService userPreferenceService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    



/*  
    public void processNotification(Notification request) {
        // Step 1: Get user preferences
        UserNotificationPreference preferences = 
            userPreferenceService.getUserPreferences(request.getUserId(), request.getEventType());
        
        // Step 2: Create notifications for enabled channels
        List<Notification> notifications = createNotifications(request, preferences);
        
        // Step 3: Save notifications
        notifications = notificationRepository.saveAll(notifications);
        
        // Step 4: Send notifications asynchronously
        notifications.forEach(this::sendNotificationAsync);
    }


 */
  
    
    private List<Notification> createNotifications(Notification request, 
                                                  UserNotificationPreference preferences) {
        List<Notification> notifications = new ArrayList<>();
        /*
        if (preferences.isEmailEnabled()) {
            notifications.add(createNotification(request, NotificationType.EMAIL));
        }
        
        if (preferences.isSmsEnabled()) {
            notifications.add(createNotification(request, NotificationType.SMS));
        }
        
        if (preferences.isPushEnabled()) {
            notifications.add(createNotification(request, NotificationType.PUSH));
        }*/
        
        return notifications;
    }
    

    private Notification createNotification(Notification request, NotificationType type) {
        /*
        NotificationTemplate template = templateService.getTemplate(request.getEventType(), type);
        
        String content = templateService.processTemplate(template, request.getData());
        
        return Notification.builder()
            .id(UUID.randomUUID().toString())
            .userId(request.getUserId())
            .type(type.name())
            .channel(request.getEventType())
            .title(template.getSubject())
            .content(content)
            .templateData(request.getData())
            .status(NotificationStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .retryCount(0)
            .build();
    */
    return null;
            }
    

    @Async
    public void sendNotificationAsync(Notification notification) {
        /*
        try {
            NotificationChannel channel = channelFactory.getChannel(notification.getType());
            boolean sent = channel.send(notification);
            
            if (sent) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setFailureReason("Channel delivery failed");
            }
            
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
        } finally {
            notificationRepository.save(notification);
            publishDeliveryStatus(notification);
        }
             */
    }
    

    private void publishDeliveryStatus(Notification notification) {
        /*        NotificationDeliveryEvent event = NotificationDeliveryEvent.builder()
            .notificationId(notification.getId())
            .userId(notification.getUserId())
            .status(notification.getStatus())
            .timestamp(LocalDateTime.now())
            .build();
        
        kafkaTemplate.send("notification-delivery", event);
    */
        }
        
}