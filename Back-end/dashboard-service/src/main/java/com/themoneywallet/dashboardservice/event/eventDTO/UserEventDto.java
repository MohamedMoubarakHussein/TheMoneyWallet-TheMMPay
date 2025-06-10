package com.themoneywallet.dashboardservice.event.eventDTO;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEventDto {

 
    private String id;

  
    private String userName;

  
    private String firstName;

  
    private String lastName;

    
   
    private String email;

   
    private String userRole;

    private boolean locked;
    private boolean enabled;

   
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

 
    private boolean emailNotificationsEnabled ;
    private String language ;
    private String timezone ;
    

   
}
