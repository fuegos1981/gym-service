package com.gym.crm.security;

import com.gym.crm.exception.AccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenAuthenticatorTest {

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "testPassword";
    private static final String TOKEN = "mockJwtToken";

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private CustomAuthenticationProvider authenticationProvider;

    @InjectMocks
    private TokenAuthenticator tokenAuthenticator;

    @Test
    void checkIfAuthenticateIsWorked() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(mockUserDetails, null));
        when(jwtProvider.generateToken(mockUserDetails)).thenReturn(TOKEN);

        String actual = tokenAuthenticator.authenticate(USERNAME, PASSWORD);

        assertEquals(TOKEN, actual);
        verify(authenticationProvider).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtProvider).generateToken(mockUserDetails);
    }

    @Test
    public void checkIfAuthenticateWithInvalidCredentials() {
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        Exception exception = assertThrows(AccessException.class, () -> tokenAuthenticator.authenticate(USERNAME, PASSWORD));

        assertTrue(exception.getMessage().contains("Invalid credentials"));
        verify(authenticationProvider).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}