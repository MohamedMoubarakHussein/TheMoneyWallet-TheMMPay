package com.themoneywallet.notificationservice.repository;

import java.util.List;

import com.themoneywallet.notificationservice.entity.Notification;

public interface NotificationRepository {

    List<Notification> saveAll(List<Notification> notifications);
    
}
