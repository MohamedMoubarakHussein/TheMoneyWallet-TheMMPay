package com.themoneywallet.usermanagmentservice.controller;

import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.usermanagmentservice.dto.request.UserRequest;
import com.themoneywallet.usermanagmentservice.dto.request.UserUpdate;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.service.UserService;
import com.themoneywallet.usermanagmentservice.utilite.ValidationErrorMessageConverter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final ValidationErrorMessageConverter validConvertor;    
    private final UserService userService;

    //TODO currently we handling database data violation in the backend not in the database itself
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserRequest user , BindingResult result){
        log.info("xsxss");
        if(result.hasErrors()){
            return  ResponseEntity.badRequest().body(this.validConvertor.Convert(result));
        }
         
        return this.userService.signUp(user);
    }

    @PutMapping("/updateuser")
    public ResponseEntity<String> updateUser(@RequestParam("email") String email ,@Valid @RequestBody UserUpdate user ,BindingResult result){
        if(result.hasErrors()){
            return  ResponseEntity.badRequest().body(this.validConvertor.Convert(result));
        }
        return this.userService.updateUser(email,user);
    }

    @GetMapping("/getbyemail")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getUserByEmail(@RequestParam("email") String email){
       return this.userService.getUserByEmail(email);
    }
    
    @GetMapping("/getbyusername")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getUserByUserName(@RequestParam("usr") String userName){
       return this.userService.getUserByUserName(userName);
    }

    @DeleteMapping("/deletebyemail")
    public ResponseEntity<String> deleteUser(@RequestParam("email") String email){
       return this.userService.deleteUser(email);
    }

    @GetMapping("/get")
    public Iterable<User> returnAll(){
        return this.userService.returnAll();
    }

    @PutMapping("/rest")
    public ResponseEntity<String> restPassword(@RequestParam("email") String email,@Valid @RequestBody UserUpdate user ,BindingResult result){
        if(result.hasErrors()){
            return  ResponseEntity.badRequest().body(this.validConvertor.Convert(result));
        }
        return this.userService.updateUser(email, user);
    }
    
}
