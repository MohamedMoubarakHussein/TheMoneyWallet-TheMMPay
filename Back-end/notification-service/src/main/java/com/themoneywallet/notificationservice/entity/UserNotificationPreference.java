package com.themoneywallet.notificationservice.entity;

import java.time.LocalDateTime;

import com.themoneywallet.notificationservice.event.EventType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "user_notification_preferences")
public class UserNotificationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String userId;
    private EventType eventType;
    private boolean emailEnabled;
    private boolean smsEnabled;
    private boolean pushEnabled;
    private LocalDateTime updatedAt;
}