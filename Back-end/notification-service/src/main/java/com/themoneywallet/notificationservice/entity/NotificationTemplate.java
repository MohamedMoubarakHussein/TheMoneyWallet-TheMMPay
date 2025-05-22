package com.themoneywallet.notificationservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_templates")
public class NotificationTemplate {
    @Id
    private String id;
    private String eventType;
    private String channel; // EMAIL, SMS, PUSH
    private String subject;
    private String bodyTemplate;
    private String language;
    private boolean active;
}