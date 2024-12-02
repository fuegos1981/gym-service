package com.gym.crm.security;

import com.gym.crm.exception.ServiceException;
import com.gym.crm.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "correctPassword";

    @Mock
    private BruteForceProtector bruteForceProtector;

    @Mock
    private CustomPasswordEncoder passwordEncoder;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private CustomAuthenticationProvider authenticationProvider;

    @Test
    void checkIfAuthenticateWithCorrectLoginAndPassword() {
        when(bruteForceProtector.isBlocked(USERNAME)).thenReturn(false);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn(PASSWORD);
        when(passwordEncoder.matches(PASSWORD, PASSWORD)).thenReturn(true);

        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);

        Authentication result = authenticationProvider.authenticate(authenticationRequest);

        assertNotNull(result);
        verify(bruteForceProtector).loginSucceeded(USERNAME);
    }

    @Test
    public void checkIfAuthenticateWithUserBlocked() {
        when(bruteForceProtector.isBlocked(USERNAME)).thenReturn(true);
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);

        Exception exception = assertThrows(ServiceException.class, () -> authenticationProvider.authenticate(authenticationRequest));

        assertEquals("User is temporarily locked due to multiple failed login attempts", exception.getMessage());
        verify(bruteForceProtector, never()).loginSucceeded(USERNAME);
    }

    @Test
    public void checkIfAuthenticateWithUsernameNotFound() {
        when(bruteForceProtector.isBlocked(USERNAME)).thenReturn(false);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenThrow(new UsernameNotFoundException("User not found"));

        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);

        Exception exception = assertThrows(AuthenticationException.class, () -> authenticationProvider.authenticate(authenticationRequest));

        assertTrue(exception instanceof UsernameNotFoundException);
        assertEquals("User not found", exception.getMessage());
    }
}