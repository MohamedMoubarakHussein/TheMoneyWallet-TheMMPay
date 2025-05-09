package com.themoneywallet.usermanagmentservice.dto.request;

import com.themoneywallet.usermanagmentservice.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
    
    @Size(min=4,max=64,message = "email should be between 4 and 64 characters.")
    @Email(message = "You should put a vaild email address.")
    private String email;

    @Size(min=8,max=32,message = "password should be between 8 and 32 characters.")
    private String password;

    private Role role;
}
