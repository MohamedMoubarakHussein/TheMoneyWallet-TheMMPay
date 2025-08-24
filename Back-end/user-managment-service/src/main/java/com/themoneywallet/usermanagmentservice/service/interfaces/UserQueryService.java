package com.themoneywallet.usermanagmentservice.service.interfaces;

import com.themoneywallet.usermanagmentservice.dto.response.UserPublicProfile;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * Interface for user query operations
 * Follows Interface Segregation Principle
 */
public interface UserQueryService {
    
    /**
     * Retrieves user profile by token
     */
    ResponseEntity<UnifiedResponse> getUserProfile(String token);
    
    /**
     * Retrieves public profile by username
     */
    ResponseEntity<UnifiedResponse> getPublicProfile(String userName);
    
    /**
     * Finds user by email
     */
    Optional<User> findUserByEmail(String email);
    
    /**
     * Finds user by username
     */
    Optional<User> findUserByUsername(String userName);
}

