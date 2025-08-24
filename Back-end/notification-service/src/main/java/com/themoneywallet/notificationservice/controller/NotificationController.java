package com.themoneywallet.notificationservice.controller;

import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/get")
    public ResponseEntity<UnifiedResponse> getUserNotifications(
        @RequestHeader("Authorization") String token,
        @RequestParam(defaultValue = "false") boolean unreadOnly
    ) {
        return unreadOnly
            ? notificationService.getUnreadNotifications(token)
            : notificationService.getAllUserNotifications(token);
    }

    @PatchMapping("/{id}/mark-read")
    public void markAsRead( @RequestHeader("Authorization") String token,@PathVariable String NotificationId) {
        notificationService.markNotificationRead(token , NotificationId);
    }
}
