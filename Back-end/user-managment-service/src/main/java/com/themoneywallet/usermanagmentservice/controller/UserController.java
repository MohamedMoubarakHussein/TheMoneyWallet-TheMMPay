package com.themoneywallet.usermanagmentservice.controller;

import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.usermanagmentservice.dto.request.UserRequest;
import com.themoneywallet.usermanagmentservice.dto.request.UserUpdateRequest;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.service.UserService;
import com.themoneywallet.usermanagmentservice.utilite.ValidationErrorMessageConverter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final ValidationErrorMessageConverter validConvertor;    
    private final UserService userService;

    //TODO currently we handling database data violation in the backend not in the database itself
    private ResponseEntity<String> signUp(@Valid @RequestBody UserRequest user , BindingResult result){
        log.info("xsxss");
        if(result.hasErrors()){
            return  ResponseEntity.badRequest().body(this.validConvertor.Convert(result));
        }
         
        return this.userService.signUp(user);
    }



    @PatchMapping("/updateuser")
    public ResponseEntity<String> updateUser(HttpServletRequest request  ,@Valid @RequestBody UserUpdateRequest user ,BindingResult result){
        if(result.hasErrors()){
            return  ResponseEntity.badRequest().body(this.validConvertor.Convert(result));
        }
        return this.userService.updateUser(request,user);
    }



    @GetMapping("/getbyemail")
    public String getUserByEmail(@RequestParam("email") String email){
       return this.userService.getUserByEmail(email);
    }

     @GetMapping("/getidbytoken")
    public String getIdByToken(@RequestHeader("Authorization") String token){
       return this.userService.getIdByToken(token.substring(7));
    }
    
    @GetMapping("/getbyusername")
    public String getUserByUserName(@RequestParam("user") String userName){
       return this.userService.getUserByUserName(userName);
    }

    @DeleteMapping("/deletebyemail")
    public ResponseEntity<String> deleteUser(@RequestParam("email") String email){
       return this.userService.deleteUser(email);
    }

    @GetMapping("/getAll")
    public Iterable<User> returnAll(){
        return this.userService.returnAll();
    }

  /*   @PutMapping("/rest")
    public ResponseEntity<String> restPassword(@RequestParam("email") String email,@Valid @RequestBody UserUpdateRequest user ,BindingResult result){
        if(result.hasErrors()){
            return  ResponseEntity.badRequest().body(this.validConvertor.Convert(result));
        }
        return this.userService.updateUser(email, user);
    }*/

      @GetMapping("/{userId}")
    public ResponseEntity<String> getUserProfile(@PathVariable String userId) {
            
        return this.userService.getUserById(userId);
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUserProfile(
            @PathVariable String userId,
            @RequestBody @Valid UserUpdateRequest request) {
            
        
        return userService.updateUserProfile(userId, request);
    }
    
 /* 
    @PutMapping("/{userId}/role")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable String userId,
            @RequestBody UserUpdateRequest request) {
            
       
        return userService.updateUserRole(userId, request.getRole());
    }
    */


   
    
 
    
}
