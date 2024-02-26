package com.themoneywallet.authenticationservice.controller;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.response.JwtToken;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.service.implementation.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<JwtToken> Register(@RequestBody SignUpRequest user){
        return this.authService.signUp(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtToken> signIn(@RequestBody AuthRequest user){
        return this.authService.signIn(user);
    }

    @GetMapping("/vaildate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token){
         this.authService.validToken(token);
        return new ResponseEntity<>("Token is valid" , HttpStatus.OK);
    }




}
