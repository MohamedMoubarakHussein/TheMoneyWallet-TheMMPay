package com.themoneywallet.sharedUtilities.dto.event;


import com.themoneywallet.sharedUtilities.enums.UserRole;

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
    private String userId;

    private String userName;

    private String firstName;

    private String lastName;

    private String email;

    private UserRole userRole;

    private boolean locked;
    private boolean enabled;

    private String emailVerficationCode;
}
