package com.themoneywallet.authenticationservice.service.implementation;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    
    
    private final UserCredentialRepository userCredentialRepository;
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<UserCredential> user = this.userCredentialRepository.findByUserName(userName);
        if(user.isPresent()){
            return user.get();
        }else{
            throw new UsernameNotFoundException("UserName Not Found");
        }
    }
    
}
