package com.themoneywallet.usermanagmentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInformation {
    
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
