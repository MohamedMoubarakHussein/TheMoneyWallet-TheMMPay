package com.themoneywallet.usermanagmentservice.controller;

import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.usermanagmentservice.dto.request.UserRequest;
import com.themoneywallet.usermanagmentservice.service.UserService;
import com.themoneywallet.usermanagmentservice.utilite.ValidationErrorMessageConverter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    private final ValidationErrorMessageConverter validConvertor;    
    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> signUp(@Valid @RequestBody UserRequest user , BindingResult result){
        if(result.hasErrors()){
            return  ResponseEntity.badRequest().body(this.validConvertor.Convert(result));
        }
         
        return this.userService.signUp(user);
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

     @GetMapping("/getbyemail1")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String updateUser(@RequestParam("email") String email){
       return "";
    }

 @GetMapping("/getbyemail2")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String deleteUser(@RequestParam("email") String email){
       return "";
    }
    
}
