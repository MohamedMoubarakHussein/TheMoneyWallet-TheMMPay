package com.themoneywallet.authenticationservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLogInEvent {

    private String id;

    private String email;

    private String userRole;
}
