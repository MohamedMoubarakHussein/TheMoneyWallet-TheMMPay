package com.themoneywallet.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.themoneywallet.notificationservice.entity.Notification;
import com.themoneywallet.notificationservice.entity.NotificationType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SMSNotificationChannel  {
    
    /* 
    private final SMSProvider smsProvider; // Twilio, AWS SNS, etc.
    
   
    

    public boolean send(Notification notification) {
        try {
           
            
            
            return smsProvider.sendSMS(
                user.getPhoneNumber(), 
                notification.getContent()
            );
            
        } catch (Exception e) {
            log.error("Failed to send SMS notification: {}", e.getMessage());
            return false;
        }
    }
    
  */
}