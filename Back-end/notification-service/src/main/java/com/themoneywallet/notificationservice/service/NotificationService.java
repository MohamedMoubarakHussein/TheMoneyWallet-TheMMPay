package com.themoneywallet.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.notificationservice.dto.response.UnifiedResponse;
import com.themoneywallet.notificationservice.entity.Notification;
import com.themoneywallet.notificationservice.entity.fixed.ResponseKey;
import com.themoneywallet.notificationservice.event.Event;
import com.themoneywallet.notificationservice.event.UserEventDto;
import com.themoneywallet.notificationservice.repository.NotificationRepository;
import com.themoneywallet.notificationservice.service.notificationservices.EmailService;
import com.themoneywallet.notificationservice.utilites.UnifidResponseHandler;
import com.themoneywallet.notificationservice.utilites.shared.JwtValidator;

import jakarta.mail.MessagingException;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final UnifidResponseHandler uResponseHandler;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final JwtValidator jwtValidator;



    private void createNotification(Event event) {
         Notification notification = Notification.builder()
                           .id(UUID.randomUUID().toString())
                           .userId(null)
                           .title(null)
                           .message(null)
                           .type(null)
                           .status(null)
                           .metadata(null).build();
        this.notificationRepo.save(notification);
    }

    public ResponseEntity<UnifiedResponse> getAllUserNotifications(String token) {
        String userId = this.jwtValidator.getUserId(token);
        if(userId == null)
            return this.uResponseHandler.generateFailedResponse("error", "your token has expired. please logout and login again.", "NOTK11001");
        return this.uResponseHandler.generateSuccessResponse( "notification",notificationRepo.findByUserId(userId), HttpStatus.OK);
    }

    public ResponseEntity<UnifiedResponse> getUnreadNotifications(String token) {
        String userId = this.jwtValidator.getUserId(token);
        if(userId == null)
            return this.uResponseHandler.generateFailedResponse("error", "your token has expired. please logout and login again.", "NOTK11002");
        return this.uResponseHandler.generateSuccessResponse( "notification",notificationRepo.findByUserIdAndReadFalse(userId), HttpStatus.OK);
            
    }
 
    public void markNotificationRead(String token ,String  NotificationId) {
       
       String userId = this.jwtValidator.getUserId(token);
        notificationRepo
            .findById(NotificationId)
            .ifPresent(notification -> {
                if(userId == notification.getUserId()){
                notification.setRead(true);
                notificationRepo.save(notification);
                }
            });
    }

    public void sendEmailVerfication(Event event) {

          
        UserEventDto user;
        try {
            user = this.objectMapper.readValue(event.getAdditionalData().get(ResponseKey.DATA.toString()).get("data"), UserEventDto.class);
            
        } catch (Exception e) {

            return;
        } 
        String title = "TheMMPay - Verfification code  ";
       /*try {

        Context context = new Context();
        context.setVariable("name", user.getFirstName());
        context.setVariable("code", user.getEmailVerficationCode());
        this.emailService.sendTemplateEmail(user.getEmail(), title, context, "verficationEmail");
         } catch (MessagingException e) {
        return;
        }*/
    }




       

}
