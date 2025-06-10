package com.themoneywallet.authenticationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.response.UnifiedResponse;
import com.themoneywallet.authenticationservice.service.implementation.AuthService;
import com.themoneywallet.authenticationservice.utilities.ValidtionRequestHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final ValidtionRequestHandler validtionRequestHandlerhandler;

    @PostMapping(value = "/signup")
    public ResponseEntity<UnifiedResponse> Register(
        @Valid @RequestBody(required = false) SignUpRequest user,
        BindingResult result,
        HttpServletRequest req
    ) {
        log.info("csdacvasd " + user.toString());
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.validtionRequestHandlerhandler.handle(result),
                HttpStatus.BAD_REQUEST
            );
        }

        return this.authService.signUp(user, req);
    }

    @PostMapping("/signin")
    public ResponseEntity<UnifiedResponse> signIn(
        @Valid @RequestBody AuthRequest user,
        BindingResult result,
        HttpServletRequest req
    ) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.validtionRequestHandlerhandler.handle(result),
                HttpStatus.BAD_REQUEST
            );
        }

        return this.authService.signIn(user, req);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<String> refresh(
        @RequestHeader("Authorization") String token,
        HttpServletRequest req
    ) throws JsonProcessingException {
        return this.authService.refreshToken(token, req);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request)
        throws JsonProcessingException {
        return this.authService.logout(request);
    }
}
