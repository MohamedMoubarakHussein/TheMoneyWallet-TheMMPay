package com.themoneywallet.dashboardservice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.themoneywallet.dashboardservice.entity.UserNotification;

public interface UserNotificationRepository extends CrudRepository<UserNotification , String> {
    List<UserNotification> findByUserId(String userId);

}
