package com.gym.crm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    private JwtProvider jwtProvider;

    private String token;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider("Secret hhgjhkjlljkk jgjjkok jjhkjk kjkjkjkjkj");
        when(userDetails.getUsername()).thenReturn("testUser");
        token = jwtProvider.generateToken(userDetails);
    }

    @Test
    void checkIfGenerateTokenIsWorked() {
        String actual = jwtProvider.generateToken(userDetails);
        assertNotNull(actual);
    }

    @Test
    void checkIfExtractUsernameReturnCorrectUsername() {
        String extractedUsername = jwtProvider.extractUsername(token);
        assertEquals("testUser", extractedUsername);
    }

    @Test
    void checkIfExtractExpirationReturnCorrectExpiration() {
        Date expirationDate = jwtProvider.extractExpiration(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        boolean isValid = jwtProvider.validateToken(token, userDetails);
        assertTrue(isValid);
    }
}