package com.gym.analytics.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {
    private static final String USERNAME = "testuser";
    private static final String SECRET = "verysecretkey12345678901234567890";

    private JwtProvider jwtProvider;
    private String token;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SECRET);
        token = generateTestToken(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
    }

    @Test
    void checkIfExtractUsernameReturnCorrectUsername() {
        String extractedUsername = jwtProvider.extractUsername(token);
        assertEquals(USERNAME, extractedUsername);
    }

    @Test
    void checkIfExtractExpirationReturnCorrectExpiration() {
        Date expirationDate = jwtProvider.extractExpiration(token);
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        when(userDetails.getUsername()).thenReturn(USERNAME);
        boolean isValid = jwtProvider.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    private String generateTestToken(Date expirationDate) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

        return Jwts.builder()
                .subject(USERNAME)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }
}