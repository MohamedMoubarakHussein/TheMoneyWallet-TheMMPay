package com.themoneywallet.usermanagmentservice.controller;

import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.usermanagmentservice.dto.request.UserRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins =   "*")
public class UserController {
    
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public String signUp(@RequestBody UserRequest user){
        return "";
    }


    @GetMapping("/getbyemail")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getUserByEmail(@RequestParam("email") String email){
        return "zz";
    }

      @GetMapping("/z")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String getUserByEmailz(){
        return "zz";
    }
    
}
