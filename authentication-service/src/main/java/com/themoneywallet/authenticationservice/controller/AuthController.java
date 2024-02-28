package com.themoneywallet.authenticationservice.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.service.implementation.AuthService;
import com.themoneywallet.authenticationservice.utilities.ValidtionRequestHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final ValidtionRequestHandler validtionRequestHandlerhandler;

    @PostMapping("/signup")
    public ResponseEntity<String> Register(@Valid @RequestBody SignUpRequest user, BindingResult result){
        if(result.hasErrors()){
            String body =  this.validtionRequestHandlerhandler.handle(result);
            return new ResponseEntity<>(body , HttpStatus.BAD_REQUEST);
        }
        return this.authService.signUp(user);
    }


    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@Valid @RequestBody AuthRequest user , BindingResult result){
        if(result.hasErrors()){
            return new ResponseEntity<>(this.validtionRequestHandlerhandler.handle(result) , HttpStatus.BAD_REQUEST);
        }
        return this.authService.signIn(user);
    }

    @GetMapping("/h")
    public String s(){
        return "hello";
    }





}
