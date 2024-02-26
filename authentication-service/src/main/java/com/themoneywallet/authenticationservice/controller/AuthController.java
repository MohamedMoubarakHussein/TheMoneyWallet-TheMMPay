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
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final  AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<String> Register(@RequestBody UserCredential user){
        return this.authService.save(user);
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody AuthRequest user){
        Authentication authentication = authenticationManager
                                        .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if(authentication.isAuthenticated()){
            return new ResponseEntity<>(this.authService.generateToken(user.getEmail()), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Invaild access" , HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/vaildate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token){
        this.authService.validateToken(token);
        return new ResponseEntity<>("Token is valid" , HttpStatus.OK);
    }




}
