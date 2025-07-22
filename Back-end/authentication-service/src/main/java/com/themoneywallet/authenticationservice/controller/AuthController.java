package com.themoneywallet.authenticationservice.controller;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.ResetPassword;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.service.implementation.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.utilities.ValidtionRequestHandler;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final ValidtionRequestHandler validtionRequestHandlerhandler;

    @PostMapping(value = "/signup")
    public ResponseEntity<UnifiedResponse> signUp(@Valid @RequestBody SignUpRequest user,BindingResult result,HttpServletRequest req){
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.validtionRequestHandlerhandler.handle(result),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.authService.signUp(user, req);
    }


    @PostMapping("/signin")
    public ResponseEntity<UnifiedResponse> signIn(@Valid @RequestBody AuthRequest user,BindingResult result,HttpServletRequest req){
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.validtionRequestHandlerhandler.handle(result),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.authService.signIn(user, req);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<UnifiedResponse> refresh(@CookieValue String refToken,HttpServletRequest req){
        return this.authService.refreshToken(refToken, req);
    }

    @PostMapping("/logout")
    public ResponseEntity<UnifiedResponse> logout(HttpServletRequest request){
        return this.authService.logout(request);
    }


    @PostMapping("/verfiy")
    public ResponseEntity<UnifiedResponse> verfiyEmail(@RequestHeader("Authorization") String token,@RequestParam("Usercode") String code){
        return this.authService.verfiyEmail(code , token);
    }

    @PostMapping("/resend")
    public ResponseEntity<UnifiedResponse> resendEmailToken(@RequestHeader("Authorization") String token){
        return this.authService.resendEmailToken(token);
    }

    @PostMapping("/forget")
    public ResponseEntity<UnifiedResponse> forgetPassword(@RequestHeader("Authorization") String token){
        return this.authService.resendForgetPasswordToken(token);
    }

    @PostMapping("/reset")
    public ResponseEntity<UnifiedResponse> restPassword(@RequestHeader("Authorization") String token , @Valid @RequestBody ResetPassword request,BindingResult result){
         if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.validtionRequestHandlerhandler.handle(result),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.authService.restPassword(token , request);
    }
  
 // OAuth2 endpoints
    @GetMapping("/oauth2/google")
    public void redirectToGoogleOAuth2(HttpServletResponse response) throws IOException {
        // This redirects to Google's OAuth2 authorization endpoint
        response.sendRedirect("/oauth2/authorization/google");
    }
/* 
    @GetMapping("/oauth2/callback")
    public ResponseEntity<UnifiedResponse> handleOAuth2Callback(@RequestParam String token) {
        // This endpoint can be used by frontend to retrieve the JWT token
        return ResponseEntity.ok(
            UnifiedResponse.builder()
                .message("OAuth2 authentication successful")
                .data(Map.of("token", token))
                .build()
        ); 
        return null;
    }
*/
    @GetMapping("/oauth2/callback")
    public void handleOAuth2Callback( @RequestParam String token,HttpServletResponse response) throws IOException {
        String frontendUrl = "http://localhost:4200/signup?token=" + token;
        response.sendRedirect(frontendUrl);
    }
}
