package com.themoneywallet.authenticationservice.service.implementation;

import java.time.Duration;

import java.util.Date;


import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.response.UnifiedResponse;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.entity.UserManagmentService;
import com.themoneywallet.authenticationservice.entity.UserRole;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.service.defintion.AuthServiceDefintion;
import com.themoneywallet.authenticationservice.utilities.DatabaseHelper;
import com.themoneywallet.authenticationservice.utilities.HttpHelper;
import com.themoneywallet.authenticationservice.utilities.ResponseHandller;

import lombok.RequiredArgsConstructor;

/**
 *  Auth service is the core service of the auth 
 */
@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceDefintion {

  
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final DatabaseHelper databaseHelper;
    private final HttpHelper httpHelper;
    private final ResponseHandller eResponse;
  


  
    
    
    @Override
    public ResponseEntity<UnifiedResponse> signUp(SignUpRequest request) {
       
        
        if(databaseHelper.isEmailExist(request.getEmail())){
            eResponse.addingErrorResponse("email", "This Email address is used.");
            return new ResponseEntity<>(eResponse.getCurrentResponse(), HttpStatus.BAD_REQUEST);
        }
        if(databaseHelper.isUserNameExist(request.getUserName())){
            eResponse.addingErrorResponse("userName", "This userName is used.");
            return new ResponseEntity<>(eResponse.getCurrentResponse(), HttpStatus.BAD_REQUEST);
        }

        // first part save the userCredential 
        UserCredential userCredential  =saveUserCredentialInAuthDb(request);
        
        //  second part save the rest of information in user managment service
        ResponseEntity<Void> status = saveUserCredentialInUserMangmentService(request, userCredential);
     
        if(status.getStatusCode().is2xxSuccessful()){
           return handleSuccessed(request.getEmail());
        }else{
            eResponse.addingErrorResponse("Internal", status.getStatusCode().value()+"");
            return new ResponseEntity<>(eResponse.getCurrentResponse(), HttpStatus.BAD_REQUEST);
        }
       
    
    }
    

    @Override
    public ResponseEntity<UnifiedResponse> signIn(AuthRequest request) {
        
        try{
        this.authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
       
        return handleSuccessed(request.getEmail());
    
        }catch(Exception ex){
            eResponse.addingErrorResponse("Internal", "invalid access.");
            return new ResponseEntity<>(eResponse.getCurrentResponse(), HttpStatus.BAD_REQUEST);
           

        }
      
    }

    @Override
    public boolean validToken(String token ) {
        boolean isValid = this.jwtService.isTokenValid(token) ;
        return isValid;
        
    }


    
    private ResponseEntity<UnifiedResponse> handleSuccessed(String email){
        String accToken =  this.jwtService.generateToken(email);
        String refToken =  this.jwtService.generateRefreshToken(email);
        
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refToken)
       .httpOnly(true)
       //.secure(true) // For HTTPS only
       .sameSite("Lax")
       .maxAge(Duration.ofHours(7))
       .path("/")
       .build();
       eResponse.addingResponse("token", accToken);
       return  ResponseEntity.status(HttpStatus.OK)
       .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .header("Authorization", "Bearer " + accToken)
       .body(eResponse.getCurrentResponse());
    
    }

    private UserCredential saveUserCredentialInAuthDb(SignUpRequest request){
        UserCredential credential = UserCredential.builder()
        .email(request.getEmail())
        .userName(request.getUserName())
        .password(this.passwordEncoder.encode(request.getPassword()))
        .userRole(UserRole.ROLE_USER)
        .locked(false)
        .enabled(true)
        .build();

       return this.userCredentialRepository.save(credential);
    }

   private ResponseEntity<Void> saveUserCredentialInUserMangmentService(SignUpRequest request, UserCredential credential ){
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

    return httpHelper.sendDataToUserMangmentService(info);

   }



   public ResponseEntity refreshToken(String refreshToken){
        // make sure token is valid
        if (!this.validToken(refreshToken)) {
            // change to the unified
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
        Optional<UserCredential> user = this.userCredentialRepository.findByToken(refreshToken);
        UserCredential token;
        if(user.isPresent()){
            token = user.get();
        }else{
            return ResponseEntity.badRequest().build();
        }

        if(token.getExpiryDateOfTokeInstant().before(new Date())){
            return ResponseEntity.badRequest().build();
        }

        if(token.isRevoked()){
            return ResponseEntity.badRequest().build();
        }

        // revoke the token 
        // generate new access , refresh token 
        return handleSuccessed(token.getEmail());
   }


   public ResponseEntity forgotPassword(String token) {
    // get user from the token if exist  send email with rest step
        return null;
    }

    public ResponseEntity verifyEmail(String token) {
    // validate token that send to email  if vaild change user to active
        return null;
    }


    public ResponseEntity logout( String token) {
        // 1. Revoke token
        // 2. Clear cookie
        return null;
    }
  
}
