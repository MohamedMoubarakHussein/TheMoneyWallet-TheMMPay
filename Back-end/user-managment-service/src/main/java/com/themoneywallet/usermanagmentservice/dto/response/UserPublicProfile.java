package com.themoneywallet.usermanagmentservice.dto.response;

import java.time.LocalDate;

import com.themoneywallet.usermanagmentservice.entity.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserPublicProfile {
    

    private String userName;
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePictureUrl;
    private String preferredLanguage;
    private String timezone;
    private LocalDate dateOfBirth;
    private Address address; 
    
}