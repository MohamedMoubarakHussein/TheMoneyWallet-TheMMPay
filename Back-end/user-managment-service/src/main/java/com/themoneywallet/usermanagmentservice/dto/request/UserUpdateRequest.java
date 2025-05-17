package com.themoneywallet.usermanagmentservice.dto.request;


import java.util.HashMap;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserUpdateRequest {
  
    @Size(min=4,max=16,message = "user name should be between 4 and 16 characters.")
    private String userName;

    @Size(min=4,max=16,message = "first name should be between 4 and 16 characters.")
    private String firstName;

    @Size(min=4,max=16,message = "last name should be between 4 and 16 characters.")
    private String lastName;
    


    @Size(min=8,max=32,message = "password should be between 8 and 32 characters.")
    private String password;

    @Builder.Default
    private HashMap<String , String> preferences = new HashMap<>();

}
