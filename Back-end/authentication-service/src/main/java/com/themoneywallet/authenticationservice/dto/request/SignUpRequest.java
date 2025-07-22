package com.themoneywallet.authenticationservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequest {
    
    @NotNull(message = "user name cannot be null.")
    @Size(min=4,max=32,message = "user name should be between 4 and 32 characters.")
    private String userName;
    
    @NotNull(message = "first name cannot be null.")
    @Size(min=4,max=16,message = "first name should be between 4 and 16 characters.")
    private String firstName;

    @NotNull(message = "last name cannot be null.")
    @Size(min=4,max=16,message = "last name should be between 4 and 16 characters.")
    private String lastName;

    @NotNull(message = "email cannot be null.")
    @Size(min=4,max=265)
    @Email(message = "You should put a vaild email address.")
    private String email;

    @NotNull(message = "password cannot be null.")
    @Size(min=8,max=100,message = "password should at least be 8  characters.")
    private String password;
}
