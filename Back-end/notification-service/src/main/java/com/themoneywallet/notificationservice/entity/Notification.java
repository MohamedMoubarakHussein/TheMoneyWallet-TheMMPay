package com.themoneywallet.notificationservice.entity;

import com.themoneywallet.notificationservice.entity.fixed.NotificationStatus;
import com.themoneywallet.notificationservice.entity.fixed.NotificationType;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notifications")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    private String id;
    private String userId;
    private String title;
    private String message;
    @Builder.Default()
    private boolean read = false;
    
    @Builder.Default()
    private LocalDateTime createdDate = LocalDateTime.now();
    @Builder.Default()
    private LocalDateTime readAt = LocalDateTime.now();

    private NotificationType type;        
    private NotificationStatus status; 

    private Map<String, Map<String , String>> metadata;

}
