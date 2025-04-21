package com.themoneywallet.authenticationservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.response.UnifiedResponse;
import com.themoneywallet.authenticationservice.service.implementation.AuthService;
import com.themoneywallet.authenticationservice.utilities.ResponseHandller;
import com.themoneywallet.authenticationservice.utilities.ValidtionRequestHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping( value = "/auth" , produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final ValidtionRequestHandler validtionRequestHandlerhandler;
    private final  ResponseHandller myResponse;
  
   
    @PostMapping(value = "/signup" )
    public ResponseEntity<UnifiedResponse> Register(@Valid @RequestBody SignUpRequest user, BindingResult result){
        if(result.hasErrors()){
            this.validtionRequestHandlerhandler.handle(result);
            return new ResponseEntity<>(myResponse.getCurrentResponse() , HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<UnifiedResponse> res = this.authService.signUp(user);
        return res;
    }

    @PostMapping("/signin")
    public ResponseEntity<UnifiedResponse> signIn(@Valid @RequestBody AuthRequest user , BindingResult result){
        if(result.hasErrors()){
            this.validtionRequestHandlerhandler.handle(result);
            return new ResponseEntity<>(myResponse.getCurrentResponse() , HttpStatus.BAD_REQUEST);
        }
        return this.authService.signIn(user);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity refresh(@CookieValue("refreshToken") String token) {
        return null;

    }
    

   
   @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@CookieValue("refreshToken") String token) {
         return null;
    }


   @PostMapping("/verify-email")
    public ResponseEntity verifyEmail(@CookieValue("refreshToken") String token) {
        return null;
    }




    @PostMapping("/logout")
    public ResponseEntity logout(@CookieValue(name = "refresh_token") String refreshToken) {
        return null;
    }
 





}
