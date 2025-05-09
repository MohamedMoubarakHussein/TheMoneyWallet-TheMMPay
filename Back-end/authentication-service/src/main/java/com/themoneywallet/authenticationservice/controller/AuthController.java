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
import com.themoneywallet.authenticationservice.utilities.ValidtionRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
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
   
  
   
    @PostMapping(value = "/signup" )
    public ResponseEntity<String> Register(@Valid @RequestBody SignUpRequest user, BindingResult result){
        if(result.hasErrors()){
            UnifiedResponse myResponse = new UnifiedResponse();
            this.validtionRequestHandlerhandler.handle(result ,myResponse);
            return new ResponseEntity<>(myResponse.toString() , HttpStatus.BAD_REQUEST);
        }
     
     
     
        return this.authService.signUp(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@Valid @RequestBody AuthRequest user , BindingResult result){
        if(result.hasErrors()){
            UnifiedResponse myResponse = new UnifiedResponse();
            this.validtionRequestHandlerhandler.handle(result ,myResponse);
            return new ResponseEntity<>(myResponse.toString() , HttpStatus.BAD_REQUEST);
        }

        return this.authService.signIn(user);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<UnifiedResponse> refresh(@CookieValue("refreshToken") String token) {
        return this.authService.refreshToken(token);

    }
    

   



   @PostMapping("/verify-email")
    public ResponseEntity<UnifiedResponse> verifyEmail(@CookieValue("refreshToken") String token) {
        return this.authService.verifyEmail(token);
    }




    @PostMapping("/logout")
    public ResponseEntity<UnifiedResponse> logout(HttpServletRequest request) {
        return this.authService.logout(request);
    }
 





}
