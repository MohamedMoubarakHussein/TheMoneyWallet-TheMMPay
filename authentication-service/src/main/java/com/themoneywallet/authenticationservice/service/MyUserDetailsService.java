package com.themoneywallet.authenticationservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.themoneywallet.authenticationservice.entity.MyUserDetails;
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
        return user.map(MyUserDetails::new).orElseThrow(()-> new UsernameNotFoundException("this email dosen't exist "+ email));

    }
    
}
