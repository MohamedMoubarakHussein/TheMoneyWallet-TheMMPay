package com.themoneywallet.usermanagmentservice.service.commands;

import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;
import com.themoneywallet.usermanagmentservice.domain.events.UserProfileDeletedEvent;
import com.themoneywallet.sharedUtilities.patterns.command.Command;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.utilities.JwtValidator;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.themoneywallet.sharedUtilities.service.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Command for deleting user profile
 * Follows Command Pattern and Single Responsibility Principle
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteUserProfileCommand implements Command<String, ResponseEntity<UnifiedResponse>> {
    
    private final UserRepository userRepository;
    private final JwtValidator jwtValidator;
    private final UnifidResponseHandler responseHandler;
    private final EventPublisher eventPublisher;
    
    @Override
    public ResponseEntity<UnifiedResponse> execute(String token) {
        log.info("Executing delete user profile command");
        
        String email = jwtValidator.extractUserName(token);
        
        // Find user
        Optional<User> opUser = userRepository.findByEmail(email);
        if (opUser.isEmpty()) {
            throw new BusinessException("User not found", "USER_NOT_FOUND");
        }
        
        User user = opUser.get();
        
        // Delete user
        userRepository.delete(user);
        
        // Publish domain event
        publishUserDeletedEvent(user);
        
        return responseHandler.generateSuccessResponseNoBody("data", HttpStatus.OK);
    }
    
    @Override
    public boolean canExecute(String input) {
        return input != null && !input.trim().isEmpty();
    }
    
    @Override
    public String getCommandName() {
        return "DeleteUserProfileCommand";
    }
    
    private void publishUserDeletedEvent(User user) {
        UserProfileDeletedEvent event = new UserProfileDeletedEvent(
                user.getUserId(),
                user
        );
        
        eventPublisher.publish(event);
    }
}

