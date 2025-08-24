package com.themoneywallet.usermanagmentservice.service.commands;

import com.themoneywallet.usermanagmentservice.dto.request.UserUpdateRequest;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;
import com.themoneywallet.usermanagmentservice.domain.events.UserProfileUpdatedEvent;
import com.themoneywallet.sharedUtilities.patterns.command.Command;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.utilities.JwtValidator;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.themoneywallet.sharedUtilities.service.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Command for updating user profile
 * Follows Command Pattern and Single Responsibility Principle
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserProfileCommand implements Command<UpdateUserProfileCommand.UserUpdateCommandRequest, ResponseEntity<UnifiedResponse>> {
    
    private final UserRepository userRepository;
    private final JwtValidator jwtValidator;
    private final UnifidResponseHandler responseHandler;
    private final EventPublisher eventPublisher;
    
    @Override
    public ResponseEntity<UnifiedResponse> execute(UpdateUserProfileCommand.UserUpdateCommandRequest request) {
        log.info("Executing update user profile command");
        
        String email = jwtValidator.extractUserName(request.getToken());
        
        // Find user
        Optional<User> opUser = userRepository.findByEmail(email);
        if (opUser.isEmpty()) {
            throw new BusinessException("User not found", "USER_NOT_FOUND");
        }
        
        // Update user
        User user = opUser.get();
        BeanUtils.copyProperties(request.getUpdateRequest(), user);
        user.setUpdatedAt(LocalDateTime.now());
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Publish domain event
        publishUserUpdatedEvent(savedUser);
        
        return responseHandler.generateSuccessResponse("data", savedUser, HttpStatus.OK);
    }
    
    @Override
    public boolean canExecute(UpdateUserProfileCommand.UserUpdateCommandRequest input) {
        return input != null && 
               input.getToken() != null &&
               input.getUpdateRequest() != null;
    }
    
    @Override
    public String getCommandName() {
        return "UpdateUserProfileCommand";
    }
    
    private void publishUserUpdatedEvent(User user) {
        UserProfileUpdatedEvent event = new UserProfileUpdatedEvent(
                user.getUserId(),
                user
        );
        
        eventPublisher.publish(event);
    }
    
    /**
     * Request wrapper for the command
     */
    public static class UserUpdateCommandRequest {
        private final UserUpdateRequest updateRequest;
        private final String token;
        
        public UserUpdateCommandRequest(UserUpdateRequest updateRequest, String token) {
            this.updateRequest = updateRequest;
            this.token = token;
        }
        
        public UserUpdateRequest getUpdateRequest() {
            return updateRequest;
        }
        
        public String getToken() {
            return token;
        }
    }
}
