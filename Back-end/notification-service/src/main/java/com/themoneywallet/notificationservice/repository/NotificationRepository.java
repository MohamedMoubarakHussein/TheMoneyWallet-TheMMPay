package com.themoneywallet.notificationservice.repository;

import com.themoneywallet.notificationservice.entity.Notification;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository
    extends MongoRepository<Notification, String> {
    List<Notification> findByUserId(String userId);
    List<Notification> findByUserIdAndReadFalse(String userId);
}
