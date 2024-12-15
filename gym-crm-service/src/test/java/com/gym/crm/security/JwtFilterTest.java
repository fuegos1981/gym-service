package com.gym.crm.security;

import com.gym.crm.exception.CoreError;
import com.gym.crm.service.impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    private JwtFilter jwtFilter;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private final String validGatewaySecret = "valid-secret";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtFilter = new JwtFilter(validGatewaySecret, userDetailsService, jwtProvider);
    }

    @Test
    void checkIfPassFilterWhenValidGatewayAndTokenProvided() throws ServletException, IOException {
        when(request.getHeader("Gateway")).thenReturn(validGatewaySecret);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(request.getHeader("X-USER")).thenReturn("testUser");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(mockUserDetails);
        when(mockUserDetails.getAuthorities()).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        SecurityContext context = SecurityContextHolder.getContext();
        assertNotNull(context.getAuthentication());
    }

    @Test
    void checkIfReturnUnauthorizedWhenGatewayHeaderIsMissing() throws ServletException, IOException {
        when(request.getHeader("Gateway")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(stringWriter.toString().contains("Without Gateway!"));
    }

    @Test
    void checkIfGatewayHeaderIsInvalidReturnUnauthorized() throws ServletException, IOException {
        when(request.getHeader("Gateway")).thenReturn("invalid-secret");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void checkIfHandleAuthenticationWhenNotAuthenticated() throws ServletException, IOException {
        when(request.getHeader("Gateway")).thenReturn(validGatewaySecret);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(request.getHeader("X-USER")).thenReturn("testUser");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(mockUserDetails);
        when(mockUserDetails.getAuthorities()).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService, times(1)).loadUserByUsername("testUser");
        SecurityContext context = SecurityContextHolder.getContext();
        assertNotNull(context.getAuthentication());
    }

    @Test
    void checkIfAccessExceptionReturnErrorResponse() throws ServletException, IOException {
        when(request.getHeader("Gateway")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        printWriter.flush();
        String responseBody = stringWriter.toString();

        assertTrue(responseBody.contains("Without Gateway!"));
        assertTrue(responseBody.contains(CoreError.ACCESS_ERROR.getCode()));
    }
}