package com.themoneywallet.authenticationservice.service.interfaces;

import com.themoneywallet.authenticationservice.dto.request.ResetPassword;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import org.springframework.http.ResponseEntity;

/**
 * Interface for password management operations
 * Follows Interface Segregation Principle
 */
public interface PasswordManagementService {
    
    /**
     * Initiates password reset process
     */
    ResponseEntity<UnifiedResponse> initiatePasswordReset(String token);
    
    /**
     * Resets user password with reset token
     */
    ResponseEntity<UnifiedResponse> resetPassword(String token, ResetPassword request);
    
    /**
     * Resends password reset token
     */
    ResponseEntity<UnifiedResponse> resendPasswordResetToken(String token);
}

