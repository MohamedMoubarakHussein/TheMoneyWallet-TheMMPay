package com.themoneywallet.authenticationservice.service.defintion;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.ResetPassword;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthServiceInterface {

    ResponseEntity<UnifiedResponse> signUp(SignUpRequest signUpRequest, HttpServletRequest req);

    boolean publishSignUpEvent(SignUpRequest request, UserCredential user);

    ResponseEntity<UnifiedResponse> signIn(AuthRequest authRequest, HttpServletRequest req);

    boolean publishSigninEvent(UserCredential user);

    boolean validToken(String token);

    ResponseEntity<UnifiedResponse> refreshToken(String refreshToken, HttpServletRequest req);

    ResponseEntity<UnifiedResponse> logout(HttpServletRequest request);

    ResponseEntity<UnifiedResponse> verfiyEmail(String code, String token);

    boolean isExistBeforeSignUp(Map<String, Map<String, String>> mp, SignUpRequest request);

    void revokeToken(String token);

    ResponseEntity<UnifiedResponse> resendEmailToken(String token);

    ResponseEntity<UnifiedResponse> resendForgetPasswordToken(String token);

    ResponseEntity<UnifiedResponse> restPassword(String token, ResetPassword request);

    Map<String, String> tokenGenerator(String userName, UUID userId, String userRole);

    HttpHeaders makeAuthHeaders(String cookie, String accToken);

}