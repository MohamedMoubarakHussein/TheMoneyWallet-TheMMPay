package com.themoneywallet.authenticationservice.unit.service;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.service.implementation.MyUserDetailsService;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCredentialServiceTest {

    private UserCredentialRepository userCredentialRepository;
    private MyUserDetailsService userCredentialService;

    @BeforeEach
    void setUp() {
        userCredentialRepository = mock(UserCredentialRepository.class);
        userCredentialService = new MyUserDetailsService(userCredentialRepository);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        // Arrange
        String username = "mohamed";
        UserCredential mockUser = new UserCredential(); // ensure this implements UserDetails
        mockUser.setUserName(username);

        when(userCredentialRepository.findByUserName(username))
                .thenReturn(Optional.of(mockUser));

        // Act
        UserDetails result = userCredentialService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userCredentialRepository).findByUserName(username);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String username = "not_exist_user";
        when(userCredentialRepository.findByUserName(username))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userCredentialService.loadUserByUsername(username);
        });

        verify(userCredentialRepository).findByUserName(username);
    }
}