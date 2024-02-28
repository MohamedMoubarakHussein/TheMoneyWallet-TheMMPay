package com.themoneywallet.authenticationservice.service.implementation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.entity.UserManagmentService;
import com.themoneywallet.authenticationservice.entity.UserRole;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.service.defintion.AuthServiceDefintion;
import com.themoneywallet.authenticationservice.utilities.DatabaseHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceDefintion {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final DatabaseHelper databaseHelper;
    @Override
    public ResponseEntity<String> signUp(SignUpRequest request) {
        
        if(databaseHelper.isEmailExist(request.getEmail())){
            return new ResponseEntity<>("This Email address is used." , HttpStatus.BAD_REQUEST);
        }
        if(databaseHelper.isUserNameExist(request.getUserName())){
            return new ResponseEntity<>("This userName is used." , HttpStatus.BAD_REQUEST);
        }
        // first part save the userCredential 
        UserCredential credential = UserCredential.builder()
                                    .email(request.getEmail())
                                    .userName(request.getUserName())
                                    .password(this.passwordEncoder.encode(request.getPassword()))
                                    .userRole(UserRole.ROLE_USER)
                                    .locked(false)
                                    .enabled(true)
                                    .build();
        
        this.userCredentialRepository.save(credential);
        //  second part save the rest of information in user managment service
        UserManagmentService info = UserManagmentService.builder()
                                    .email(request.getEmail())
                                    .password(credential.getPassword())
                                    .userRole(credential.getUserRole())
                                    .vaildUntil(credential.getVaildUntil())
                                    .locked(credential.isLocked())
                                    .enabled(credential.isEnabled())
                                    .userName(request.getUserName())
                                    .firstName(request.getFirstName())
                                    .lastName(request.getLastName())
                                    .id(credential.getId())
                                    .build();
        // make a call to the user managment service

        String token =  this.jwtService.generateToken(request.getEmail());
        return new ResponseEntity<>(token , HttpStatus.CREATED);
    }
    

    @Override
    public ResponseEntity<String> signIn(AuthRequest request) {
        Authentication auth ;
        try{
         auth =  this.authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        }catch(Exception ex){
            return new ResponseEntity<>("invalid access." , HttpStatus.BAD_REQUEST);

        }
        if(auth.isAuthenticated()){
            String token =  this.jwtService.generateToken(request.getEmail());
            return new ResponseEntity<>(token , HttpStatus.OK);
        }else{
            return new ResponseEntity<>("invalid access." , HttpStatus.BAD_REQUEST);

        }
    }

    @Override
    public ResponseEntity<Boolean> validToken(String Token ) {
        return new ResponseEntity<>(this.jwtService.isTokenValid(Token, null) , HttpStatus.OK);
    }
}
