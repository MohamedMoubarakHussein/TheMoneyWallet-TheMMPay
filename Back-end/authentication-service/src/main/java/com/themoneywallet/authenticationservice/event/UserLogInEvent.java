package com.themoneywallet.authenticationservice.event;

import java.time.Instant;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLogInEvent {
     
    private int id;
 
   
    
  
    private String email;


   
    private String userRole;

}
