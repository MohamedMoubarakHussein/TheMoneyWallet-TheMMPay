package com.themoneywallet.authenticationservice.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.themoneywallet.authenticationservice.dto.request.ValidateRequest;
import com.themoneywallet.authenticationservice.service.implementation.AuthService;
import com.themoneywallet.authenticationservice.utilities.ValidtionRequestHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth" )
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final ValidtionRequestHandler validtionRequestHandlerhandler;
    private Map<String , Object> responsMap = new HashMap<>();
    private Map<String, List<String>> errorsMap = new HashMap<>(); 

    @PostMapping(value = "/signup")
    public ResponseEntity Register(@Valid @RequestBody SignUpRequest user, BindingResult result){
        responsMap.clear();
        errorsMap.clear();
        if(result.hasErrors()){
             responsMap.put("errors", this.validtionRequestHandlerhandler.handle(result,errorsMap));
            return new ResponseEntity<>(responsMap , HttpStatus.BAD_REQUEST);
        }
        
        return this.authService.signUp(user);
    }


    @PostMapping("/signin")
    public ResponseEntity signIn(@Valid @RequestBody AuthRequest user , BindingResult result){
        responsMap.clear();
        errorsMap.clear();
        if(result.hasErrors()){
            responsMap.put("errors", this.validtionRequestHandlerhandler.handle(result,errorsMap));
            return new ResponseEntity<>(responsMap , HttpStatus.BAD_REQUEST);
        }
        return this.authService.signIn(user);
    }

    @PostMapping("validate")
    public ResponseEntity<Boolean> isVaild(@RequestBody ValidateRequest req){
        log.info("Entring authentication service isValid method before");
        ResponseEntity<Boolean> x =  this.authService.validToken(req.getToken());
        log.info(x.getBody()+" 00001");

        return x;
    }





}
