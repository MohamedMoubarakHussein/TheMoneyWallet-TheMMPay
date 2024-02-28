package com.themoneywallet.authenticationservice.service.implementation;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;


import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserCredentialRepository userCredentialRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserCredential> user = this.userCredentialRepository.findByEmail(email);
        if(user.isPresent()){
            return user.get();
        }else{
            throw new UsernameNotFoundException("UserName Not Found");
        }
    }
    
}
