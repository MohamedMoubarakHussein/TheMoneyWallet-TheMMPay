package com.themoneywallet.usermanagmentservice.service.interfaces;

import com.themoneywallet.usermanagmentservice.dto.request.UserUpdateRequest;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import org.springframework.http.ResponseEntity;

/**
 * Interface for user management operations
 * Follows Interface Segregation Principle
 */
public interface UserManagementService {
    
    /**
     * Updates user profile
     */
    ResponseEntity<UnifiedResponse> updateUserProfile(UserUpdateRequest request, String token);
    
    /**
     * Deletes user profile
     */
    ResponseEntity<UnifiedResponse> deleteUserProfile(String token);
}

