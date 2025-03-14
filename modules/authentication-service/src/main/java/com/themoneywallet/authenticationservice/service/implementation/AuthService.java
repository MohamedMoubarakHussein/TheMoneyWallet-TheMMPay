package com.themoneywallet.authenticationservice.service.implementation;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final WebClient webClient;
    private Map<String , Object> responsMap = new HashMap<>();
    private Map<String, List<String>> errorsMap = new HashMap<>(); 
    @Override
    public ResponseEntity signUp(SignUpRequest request) {
        responsMap.clear();
        if(databaseHelper.isEmailExist(request.getEmail())){
          
            errorsMap.computeIfAbsent("email", ls -> new ArrayList<>()).add("This Email address is used.");
            responsMap.put("errors" , errorsMap);
            return new ResponseEntity<>(responsMap, HttpStatus.BAD_REQUEST);
        }
        if(databaseHelper.isUserNameExist(request.getUserName())){
            errorsMap.computeIfAbsent("userName", ls -> new ArrayList<>()).add("This userName is used.");
            responsMap.put("errors" , errorsMap);
            return new ResponseEntity<>(responsMap, HttpStatus.BAD_REQUEST);
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
        //.post()
        String token =  this.jwtService.generateToken(request.getEmail());

         ResponseCookie cookie = ResponseCookie.from("auth-token", token)
        .httpOnly(true)
        //.secure(true) // For HTTPS only
        .sameSite("Lax")
        .maxAge(Duration.ofHours(1))
        .path("/")
        .build();
        
      
        return ResponseEntity.status(HttpStatus.CREATED)
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(responsMap);
    
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
    public ResponseEntity<Boolean> validToken(String token ) {
        boolean isValid = this.jwtService.isTokenValid(token) ;
        if(isValid){
            return new ResponseEntity<>(true , HttpStatus.OK);
        }else{
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        
    }



  
}
