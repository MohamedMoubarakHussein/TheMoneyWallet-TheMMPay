package com.themoneywallet.notificationservice.controller;

import com.themoneywallet.notificationservice.entity.Notification;
import com.themoneywallet.notificationservice.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<String> getUserNotifications(
        @RequestHeader("Authorization") String token,
        @CookieValue("refreshToken") String refToken,
        @RequestParam(defaultValue = "false") boolean unreadOnly
    ) {
        String userId = "1";
        return unreadOnly
            ? notificationService.getUnreadNotifications(userId)
            : notificationService.getAllUserNotifications(userId);
    }

    @PatchMapping("/{id}/mark-read")
    public void markAsRead(@PathVariable String id) {
        notificationService.markNotificationRead(id);
    }
}
