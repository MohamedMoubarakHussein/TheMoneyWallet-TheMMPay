package com.themoneywallet.authenticationservice.dto.request;

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
public class AuthRequest {

    @NotNull(message = "username cannot be null.")
    @Size(min = 4,max = 32,message = "username should be between 4 and 32 characters.")
    private String userName;

    @NotNull(message = "password cannot be null.")
    @Size(min = 8,max = 100,message = "password should be at least 8 characters.")
    private String password;
}
