package com.themoneywallet.usermanagmentservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class UserRequest {

    @NotNull(message = "user name cannot be null.")
    @Size(min=4,max=16,message = "user name should be between 4 and 16 characters.")
    private String userName;
    
    @NotNull(message = "first name cannot be null.")
    @Size(min=4,max=16,message = "first name should be between 4 and 16 characters.")
    private String firstName;

    @NotNull(message = "last name cannot be null.")
    @Size(min=4,max=16,message = "last name should be between 4 and 16 characters.")
    private String lastName;

    @NotNull(message = "email cannot be null.")
    @Size(min=4,max=64,message = "email should be between 4 and 64 characters.")
    @Email(message = "You should put a vaild email address.")
    private String email;

    @NotNull(message = "password cannot be null.")
    @Size(min=8,max=32,message = "password should be between 8 and 32 characters.")
    private String password;
}
