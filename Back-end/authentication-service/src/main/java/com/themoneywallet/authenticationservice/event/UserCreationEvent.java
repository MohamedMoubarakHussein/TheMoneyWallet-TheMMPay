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
public class UserCreationEvent {

    private String id;

    private String userName;

    private String firstName;

    private String lastName;

    private String email;

    private String userRole;

    private boolean locked;
    private boolean enabled;

    private String emailVerficationCode;
}
