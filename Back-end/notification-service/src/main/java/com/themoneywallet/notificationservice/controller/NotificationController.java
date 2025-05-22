package com.themoneywallet.notificationservice.controller;

import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.notificationservice.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    /*
  
    private final NotificationService notificationService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponse<NotificationDTO>> getUserNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PagedResponse<NotificationDTO> notifications = 
            notificationService.getUserNotifications(userId, page, size);
        
        return ResponseEntity.ok(notifications);
    }
    
    @PostMapping("/mark-read/{notificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/preferences/{userId}")
    public ResponseEntity<UserNotificationPreferenceDTO> getUserPreferences(
            @PathVariable String userId) {
        
        UserNotificationPreferenceDTO preferences = 
            notificationService.getUserPreferences(userId);
        
        return ResponseEntity.ok(preferences);
    }
    
    @PutMapping("/preferences/{userId}")
    public ResponseEntity<Void> updateUserPreferences(
            @PathVariable String userId,
            @RequestBody UserNotificationPreferenceDTO preferences) {
        
        notificationService.updateUserPreferences(userId, preferences);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(
            @RequestBody NotificationRequest request) {
        
        notificationService.processNotification(request);
        return ResponseEntity.accepted().build();
    }
         */
}