package com.themoneywallet.notificationservice.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.themoneywallet.notificationservice.entity.Notification;

@Repository
public interface NotificationRepository {

    List<Notification> saveAll(List<Notification> notifications);
    
}
