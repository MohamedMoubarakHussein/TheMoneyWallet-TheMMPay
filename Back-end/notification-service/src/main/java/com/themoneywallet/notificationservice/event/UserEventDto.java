package com.themoneywallet.notificationservice.event;


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
    private boolean locked;
    private boolean enabled ;  
   
    private String userRole;

  


    private String emailVerficationCode;

    

   
}
