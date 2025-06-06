package com.themoneywallet.usermanagmentservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.usermanagmentservice.dto.request.UserUpdateRequest;
import com.themoneywallet.usermanagmentservice.service.UserService;
import com.themoneywallet.usermanagmentservice.utilite.ValidtionRequestHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ValidtionRequestHandler validtionRequestHandlerhandler;
    private final ObjectMapper objectMapper;

    @GetMapping("/getbyemail")
    // @PreAuthorize("@mySecurity.checkCustomAccess()")
    public ResponseEntity<String> getUserByEmail(
        @RequestParam("email") String email,
        @RequestHeader("Authorization") String token
    ) throws JsonProcessingException {
        return this.userService.getUserByEmail(email);
    }

    @GetMapping("/getidbytoken")
    public ResponseEntity<String> getIdByToken(
        @RequestHeader("Authorization") String token,
        @CookieValue("refreshToken") String refToken
    ) throws JsonProcessingException {
        log.info("Inside the userman service" + token);
        return this.userService.getIdByToken(token, refToken);
    }

    @GetMapping("/userprefernce")
    public ResponseEntity<String> getUserPrefernce(
        @RequestHeader("Authorization") String token
    ) {
        return this.userService.getUserPrefernce(token);
    }

    @GetMapping("/getbyusername")
    public ResponseEntity<String> getUserByUserName(
        @RequestParam("user") String userName
    ) {
        return this.userService.getUserByUserName(userName);
    }

    @GetMapping("/getAll")
    // @PreAuthorize("@mySecurity.checkCustomAccess()") must be admin
    public ResponseEntity<String> returnAll() {
        return this.userService.returnAll();
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getUserProfile(
        @RequestHeader("Authorization") String token
    ) {
        return this.userService.getUserProfile(token);
    }

    @GetMapping("role")
    public ResponseEntity<String> getUserRoles(
        @RequestHeader("Authorization") String token
    ) {
        return userService.getUserRole(token);
    }

    @PatchMapping("/updateuser")
    public ResponseEntity<String> updateUserProfile(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody UserUpdateRequest user,
        BindingResult result
    ) throws JsonProcessingException {
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.objectMapper.writeValueAsString(
                        this.validtionRequestHandlerhandler.handle(result)
                    ),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.userService.updateUser(token, user);
    }

    @PatchMapping("/updateuserpref")
    public ResponseEntity<String> updateUserPrfernce(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody UserUpdateRequest user,
        BindingResult result
    ) throws JsonProcessingException {
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.objectMapper.writeValueAsString(
                        this.validtionRequestHandlerhandler.handle(result)
                    ),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.userService.updateUserPrfernce(token, user);
    }

    @PatchMapping("/role")
    public ResponseEntity<String> updateUserRoles(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody UserUpdateRequest user,
        BindingResult result
    ) throws JsonProcessingException {
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.objectMapper.writeValueAsString(
                        this.validtionRequestHandlerhandler.handle(result)
                    ),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.userService.updateUserRole(token, user);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(String token) {
        return this.userService.deleteUser(token);
    }
}
