package com.themoneywallet.authenticationservice.utilities;

import org.springframework.stereotype.Component;

import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseHelper {

    private final UserCredentialRepository repository;
    
    public boolean isEmailExist(String email){
        return this.repository.findByEmail(email).isPresent();
    }

    public boolean isUserNameExist(String userName){
        return this.repository.findByUserName(userName).isPresent();
    }

}
