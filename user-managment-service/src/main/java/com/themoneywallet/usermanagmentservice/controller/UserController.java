package com.themoneywallet.usermanagmentservice.controller;

import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.usermanagmentservice.dto.request.UserRequest;

import lombok.RequiredArgsConstructor;

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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getUserByEmail(@RequestParam("email") String email){
       return "";
    }

  @GetMapping("/getbyusername")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getUserByUserName(@RequestParam("usr") String email){
       return "";
    }

     @GetMapping("/getbyemail")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String updateUser(@RequestParam("email") String email){
       return "";
    }

 @GetMapping("/getbyemail")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String deleteUser(@RequestParam("email") String email){
       return "";
    }
    
}
