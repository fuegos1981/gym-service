package com.gym.analytics.security;

import com.gym.analytics.service.impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
    private final static String username = "testUser";

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        SecurityContextHolder.clearContext();

        Field privateField = JwtFilter.class.getDeclaredField("username");
        privateField.setAccessible(true); // Make the field accessible

        privateField.set(jwtFilter, username);
    }

    @Test
    void checkIfDoFilterInternalWithValidToken() throws ServletException, IOException {
        String validToken = "valid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtProvider.validateToken(validToken, userDetails)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtProvider).validateToken(validToken, userDetails);
    }

    @Test
    void checkIfDoFilterInternalWithInvalidToken() throws ServletException, IOException {
        String invalidToken = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtProvider.validateToken(invalidToken, userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void checkIfDoFilterInternalWithNoAuthorizationHeader() throws ServletException, IOException {
        jwtFilter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain, times(1)).doFilter(request, response);
    }
}