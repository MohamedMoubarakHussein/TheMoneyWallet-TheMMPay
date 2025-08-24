package com.themoneywallet.authenticationservice.service.implementation;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.ResetPassword;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.service.commands.SignInCommand;
import com.themoneywallet.authenticationservice.service.commands.SignUpCommand;
import com.themoneywallet.authenticationservice.service.defintion.AuthServiceInterface;
import com.themoneywallet.authenticationservice.service.interfaces.TokenManagementService;
import com.themoneywallet.sharedUtilities.patterns.command.CommandExecutor;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.service.BaseService;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Refactored Authentication Service following SOLID principles
 * Uses Command Pattern, Dependency Injection, and separates concerns
 */
@Service
@Slf4j
public class RefactoredAuthService extends BaseService implements AuthServiceInterface {
    
    private final CommandExecutor commandExecutor;
    private final SignUpCommand signUpCommand;
    private final SignInCommand signInCommand;
    private final TokenManagementService tokenManagementService;
    
    public RefactoredAuthService(
            UnifidResponseHandler responseHandler,
            EventPublisher eventPublisher,
            CommandExecutor commandExecutor,
            SignUpCommand signUpCommand,
            SignInCommand signInCommand,
            TokenManagementService tokenManagementService) {
        super(responseHandler, eventPublisher);
        this.commandExecutor = commandExecutor;
        this.signUpCommand = signUpCommand;
        this.signInCommand = signInCommand;
        this.tokenManagementService = tokenManagementService;
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> signUp(SignUpRequest signUpRequest, HttpServletRequest req) {
        return executeOperation("signUp", signUpRequest, input -> 
                commandExecutor.execute(signUpCommand, input));
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> signIn(AuthRequest authRequest, HttpServletRequest req) {
        return executeOperation("signIn", authRequest, input -> 
                commandExecutor.execute(signInCommand, input));
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> refreshToken(String refreshToken, HttpServletRequest req) {
        return executeOperation("refreshToken", refreshToken, token -> {
            var tokens = tokenManagementService.refreshTokens(token);
            var headers = tokenManagementService.createAuthHeaders(
                    tokens.get("refreshToken"), tokens.get("accessToken"));
            return responseHandler.generateSuccessResponseNoBody("data", 
                    org.springframework.http.HttpStatus.OK, headers);
        });
    }
    
    @Override
    public boolean validToken(String token) {
        return tokenManagementService.isTokenValid(token);
    }
    
    // TODO: Implement remaining methods with proper command pattern
    // For brevity, showing the pattern with key methods
    
    @Override
    public ResponseEntity<UnifiedResponse> logout(HttpServletRequest request) {
        // Implementation would use LogoutCommand
        throw new UnsupportedOperationException("To be implemented with LogoutCommand");
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> verfiyEmail(String code, String token) {
        // Implementation would use EmailVerificationCommand
        throw new UnsupportedOperationException("To be implemented with EmailVerificationCommand");
    }
    
    // Temporary implementations for interface compliance - to be refactored
    @Override
    public boolean publishSignUpEvent(SignUpRequest request, UserCredential user) {
        // This is now handled by domain events in commands
        return true;
    }
    
    @Override
    public boolean publishSigninEvent(UserCredential user) {
        // This is now handled by domain events in commands
        return true;
    }
    
    @Override
    public boolean isExistBeforeSignUp(Map<String, Map<String, String>> mp, SignUpRequest request) {
        // This is now handled by validation services
        return false;
    }
    
    @Override
    public void revokeToken(String token) {
        tokenManagementService.revokeToken(token);
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> resendEmailToken(String token) {
        throw new UnsupportedOperationException("To be implemented with EmailVerificationCommand");
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> resendForgetPasswordToken(String token) {
        throw new UnsupportedOperationException("To be implemented with PasswordResetCommand");
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> restPassword(String token, ResetPassword request) {
        throw new UnsupportedOperationException("To be implemented with PasswordResetCommand");
    }
    
    @Override
    public Map<String, String> tokenGenerator(String userName, UUID userId, String userRole) {
        return tokenManagementService.generateTokens(userName, userId, userRole);
    }
    
    @Override
    public HttpHeaders makeAuthHeaders(String cookie, String accToken) {
        return tokenManagementService.createAuthHeaders(cookie, accToken);
    }
}
