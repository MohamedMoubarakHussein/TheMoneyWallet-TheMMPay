package com.themoneywallet.usermanagmentservice.controller;

import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.utilities.ValidtionRequestHandler;
import com.themoneywallet.usermanagmentservice.dto.request.UserUpdateRequest;
import com.themoneywallet.usermanagmentservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ValidtionRequestHandler validtionRequestHandlerhandler;

    @GetMapping("/profile")
    public ResponseEntity<UnifiedResponse> getProfile(@RequestHeader("Authorization") String token){
        return this.userService.getProfile(token);
    }

    @PutMapping("/profile")
    public ResponseEntity<UnifiedResponse> updateProfile( @Valid @RequestBody UserUpdateRequest newUserData,BindingResult result,@RequestHeader("Authorization") String token){
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.validtionRequestHandlerhandler.handle(result),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.userService.updateProfile(newUserData ,token);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<UnifiedResponse> deleteProfile(@RequestHeader("Authorization") String token){
        return this.userService.deleteUser(token);
    }
    
    @GetMapping("/{userName}")
    public ResponseEntity<UnifiedResponse> getAnotherProfile(@PathVariable String userName){
        return this.userService.getAnotherProfile(userName);
    }





}
