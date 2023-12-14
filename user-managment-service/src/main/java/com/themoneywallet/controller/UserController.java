package com.themoneywallet.controller;

import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.dto.request.UserRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public String signUp(@RequestBody UserRequest user){
        return "";
    }


    @GetMapping("/getbyemail")
    public String getUserByEmail(@RequestParam("email") String email){
        return "";
    }
    
}
