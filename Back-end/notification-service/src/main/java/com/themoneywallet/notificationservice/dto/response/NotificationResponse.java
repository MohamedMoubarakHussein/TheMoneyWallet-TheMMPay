package com.themoneywallet.notificationservice.dto.response;

import java.time.LocalDateTime;

import com.themoneywallet.notificationservice.entity.fixed.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String id;
    private String title;
    private String message;
    private boolean read = false;

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime readAt = LocalDateTime.now();

    private NotificationType type;        

}




  

