package com.themoneywallet.dashboardservice.entity;

import java.time.LocalDateTime;

import com.themoneywallet.dashboardservice.entity.fixed.NotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "user_recent_notification",
       indexes = {
          
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String userId;
    private String title;
    private String message;
    private boolean message_read ;
    
    private LocalDateTime createdDate ;
    private LocalDateTime readAt ;

    private NotificationType type;        

}


