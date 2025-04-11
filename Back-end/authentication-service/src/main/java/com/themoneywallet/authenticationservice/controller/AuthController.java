package com.themoneywallet.authenticationservice.controller;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.request.ValidateRequest;
import com.themoneywallet.authenticationservice.dto.response.UnifiedResponse;
import com.themoneywallet.authenticationservice.service.implementation.AuthService;
import com.themoneywallet.authenticationservice.utilities.ResponseHandller;
import com.themoneywallet.authenticationservice.utilities.ValidtionRequestHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final ValidtionRequestHandler validtionRequestHandlerhandler;
    private final  ResponseHandller myResponse;
  
   
    @PostMapping(value = "/signup" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UnifiedResponse> Register(@Valid @RequestBody SignUpRequest user, BindingResult result){
        log.info("00");
        if(result.hasErrors()){
            this.validtionRequestHandlerhandler.handle(result);
            log.info("1");
            log.info(myResponse.getCurrentResponse() .toString());
            return new ResponseEntity<>(myResponse.getCurrentResponse() , HttpStatus.BAD_REQUEST);
        }
        log.info("12");
        ResponseEntity<UnifiedResponse> res = this.authService.signUp(user);
        log.info("122");
        log.info(res.getBody().toString());
        return res;
    }










    @PostMapping("/signin")
    public ResponseEntity<UnifiedResponse> signIn(@Valid @RequestBody AuthRequest user , BindingResult result){
        if(result.hasErrors()){
            this.validtionRequestHandlerhandler.handle(result);
            return new ResponseEntity<>(myResponse.getCurrentResponse() , HttpStatus.BAD_REQUEST);
        }
        return this.authService.signIn(user);
    }











    @PostMapping("isvalid")
    public ResponseEntity<UnifiedResponse> isVaild(@RequestBody ValidateRequest req){
        return this.authService.validToken(req.getToken());
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token") String refreshToken) {
       return this.authService.refreshToken(refreshToken);

    }

/*
@PostMapping("/logout")
public ResponseEntity<?> logout(
    @CookieValue(name = "refresh_token") String refreshToken,
) {
    // 1. Revoke token
    refreshTokenService.revokeRefreshToken(refreshToken);
    
    // 2. Clear cookie
    ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
        .maxAge(0)
        .path("/api/auth/refresh")
        .build();
    
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body("Logged out successfully");
}
 */





}
