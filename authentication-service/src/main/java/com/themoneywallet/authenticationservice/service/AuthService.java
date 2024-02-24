package com.themoneywallet.authenticationservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ResponseEntity<String> save(UserCredential user){
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        this.userCredentialRepository.save(user);
        return new ResponseEntity<>("The user has been added "+ user.toString(),HttpStatus.CREATED );
    }

    public String generateToken(String email){
        return this.jwtService.generateToken(email);
    }

    public void validateToken(String token){
         this.jwtService.validateToken(token);
    }
}
