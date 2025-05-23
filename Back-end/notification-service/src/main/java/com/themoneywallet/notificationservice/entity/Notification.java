package com.themoneywallet.notificationservice.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String userId;
    private NotificationType type; 
    private String channel;
    private String title;
    private String content;
    private Priority priority;
    private String templateData;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private int retryCount;
    private String failureReason;
}

