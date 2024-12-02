package com.gym.crm.service.impl;

import com.gym.crm.model.User;
import com.gym.crm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder().username("user").password("password").build();
    }

    @Test
    void checkIfLoadUserByUsernameReturnUserDetailsWhenUserExists() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(testUser));

        UserDetails actual = customUserDetailsService.loadUserByUsername("user");

        assertNotNull(actual);
        assertEquals("user", actual.getUsername());
        assertEquals("password", actual.getPassword());
        assertTrue(actual.getAuthorities().isEmpty());
    }

    @Test
    void checkIfLoadUserByUsernameHasThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("nonexistentUser"));
    }

}