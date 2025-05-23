package com.themoneywallet.notificationservice.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.themoneywallet.notificationservice.entity.Notification;
import com.themoneywallet.notificationservice.entity.NotificationType;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationChannel  {
    /*
    private final JavaMailSender mailSender;
    
    
    public boolean send(Notification notification) {
        try {
            //get user
                
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo("null"); //ser.getEmail()
            helper.setSubject(notification.getTitle());
            helper.setText(notification.getContent(), true);
            helper.setFrom("noreply@yourapp.com");
            
            mailSender.send(message);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage());
            return false;
        }
    }
     */
    
}

