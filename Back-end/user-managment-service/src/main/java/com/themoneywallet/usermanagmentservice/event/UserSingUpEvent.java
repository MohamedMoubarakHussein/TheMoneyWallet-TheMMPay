package com.themoneywallet.usermanagmentservice.event;

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
public class UserSingUpEvent {
    private int id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String userRole;
    private Instant vaildUntil;
    private boolean locked;
    private boolean enabled;
    @Builder.Default
    private HashMap<String , String> preferences = new HashMap<>();
}
