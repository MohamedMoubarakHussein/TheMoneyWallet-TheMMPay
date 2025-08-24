package com.themoneywallet.authenticationservice.service.commands;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.service.interfaces.TokenManagementService;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.domain.events.UserSignedInEvent;
import com.themoneywallet.sharedUtilities.patterns.command.Command;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.themoneywallet.sharedUtilities.service.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Command for user sign in operation
 * Follows Command Pattern and Single Responsibility Principle
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SignInCommand implements Command<AuthRequest, ResponseEntity<UnifiedResponse>> {
    
    private final UserCredentialRepository userRepository;
    private final TokenManagementService tokenManagementService;
    private final AuthenticationManager authenticationManager;
    private final UnifidResponseHandler responseHandler;
    private final EventPublisher eventPublisher;
    
    private static final Integer COOKIE_MAX_AGE_H = 7;
    
    @Override
    public ResponseEntity<UnifiedResponse> execute(AuthRequest request) {
        log.info("Executing sign in command for user: {}", request.getUserName());
        
        // Authenticate user
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(), request.getPassword()));
        } catch (Exception ex) {
            throw new BusinessException("Wrong userName or password.", "AUCR1005");
        }
        
        // Update user login information
        UserCredential user = updateUserLogin(request);
        
        // Publish domain event
        publishSignInEvent(user);
        
        // Create response headers with tokens
        var headers = tokenManagementService.createAuthHeaders(
                user.getToken(), user.getAccessToken());
        
        return responseHandler.generateSuccessResponse("data", user, 
                HttpStatus.OK, headers);
    }
    
    @Override
    public boolean canExecute(AuthRequest input) {
        return input != null && 
               input.getUserName() != null && 
               input.getPassword() != null;
    }
    
    @Override
    public String getCommandName() {
        return "SignInCommand";
    }
    
    private UserCredential updateUserLogin(AuthRequest request) {
        Optional<UserCredential> opUser = userRepository.findByUserName(request.getUserName());
        
        if (opUser.isEmpty()) {
            throw new BusinessException("User not found", "AUCR1006");
        }
        
        UserCredential user = opUser.get();
        Map<String, String> tokens = tokenManagementService.generateTokens(
                user.getUsername(), user.getUserId(), user.getUserRole().toString());
        
        user.setToken(tokens.get("refreshToken"));
        user.setAccessToken(tokens.get("accessToken"));
        user.setLastLogin(LocalDateTime.now());
        user.setTokenValidTill(LocalDateTime.now().plusHours(COOKIE_MAX_AGE_H));
        user.setRevoked(false);
        
        return userRepository.save(user);
    }
    
    private void publishSignInEvent(UserCredential user) {
        UserSignedInEvent event = new UserSignedInEvent(
                user.getUserId().toString(),
                user.getEmail(),
                user.getUserRole().toString()
        );
        
        eventPublisher.publish(event);
    }
}

