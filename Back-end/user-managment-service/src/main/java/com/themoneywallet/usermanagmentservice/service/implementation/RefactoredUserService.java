package com.themoneywallet.usermanagmentservice.service.implementation;

import com.themoneywallet.usermanagmentservice.dto.request.UserUpdateRequest;
import com.themoneywallet.usermanagmentservice.service.commands.DeleteUserProfileCommand;
import com.themoneywallet.usermanagmentservice.service.commands.UpdateUserProfileCommand;
import com.themoneywallet.usermanagmentservice.service.interfaces.UserManagementService;
import com.themoneywallet.usermanagmentservice.service.interfaces.UserQueryService;
import com.themoneywallet.sharedUtilities.patterns.command.CommandExecutor;
import com.themoneywallet.sharedUtilities.patterns.observer.EventPublisher;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.service.BaseService;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Refactored User Service following SOLID principles
 * Uses Command Pattern, separates concerns into focused interfaces
 */
@Service
@Slf4j
public class RefactoredUserService extends BaseService implements UserManagementService {
    
    private final CommandExecutor commandExecutor;
    private final UpdateUserProfileCommand updateUserProfileCommand;
    private final DeleteUserProfileCommand deleteUserProfileCommand;
    private final UserQueryService userQueryService;
    
    public RefactoredUserService(
            UnifidResponseHandler responseHandler,
            EventPublisher eventPublisher,
            CommandExecutor commandExecutor,
            UpdateUserProfileCommand updateUserProfileCommand,
            DeleteUserProfileCommand deleteUserProfileCommand,
            UserQueryService userQueryService) {
        super(responseHandler, eventPublisher);
        this.commandExecutor = commandExecutor;
        this.updateUserProfileCommand = updateUserProfileCommand;
        this.deleteUserProfileCommand = deleteUserProfileCommand;
        this.userQueryService = userQueryService;
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> updateUserProfile(UserUpdateRequest request, String token) {
        UpdateUserProfileCommand.UserUpdateCommandRequest commandRequest = 
                new UpdateUserProfileCommand.UserUpdateCommandRequest(request, token);
        
        return executeOperation("updateUserProfile", commandRequest, input -> 
                commandExecutor.execute(updateUserProfileCommand, input));
    }
    
    @Override
    public ResponseEntity<UnifiedResponse> deleteUserProfile(String token) {
        return executeOperation("deleteUserProfile", token, input -> 
                commandExecutor.execute(deleteUserProfileCommand, input));
    }
    
    /**
     * Delegation methods for query operations
     */
    public ResponseEntity<UnifiedResponse> getUserProfile(String token) {
        return userQueryService.getUserProfile(token);
    }
    
    public ResponseEntity<UnifiedResponse> getPublicProfile(String userName) {
        return userQueryService.getPublicProfile(userName);
    }
}

