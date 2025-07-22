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
public class ResetPassword {
    @NotNull(message = "password cannot be null.")
    @Size(min=8,max=100,message = "password should  at least  be 8 characters.")
    private String password;

    private String forgetPasswordToken;
}
