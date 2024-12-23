package com.gym.crm.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.analytics.dto.ErrorResponse;
import com.gym.analytics.exception.AccessException;
import com.gym.analytics.exception.CoreError;
import com.gym.crm.service.impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final String gatewaySecretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtFilter(@Value("${gateway.secret}") String gatewaySecretKey, CustomUserDetailsService userDetailsService, JwtProvider jwtProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtProvider = jwtProvider;
        this.gatewaySecretKey = gatewaySecretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String username = null;

        try {
            validateGatewayHeader(request);

            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                username = request.getHeader("X-USER");
            }

            if (isNotAuthenticated(username)) {
                authenticateUser(request, username);
            }

            chain.doFilter(request, response);
        } catch (AccessException ex) {
            handleException(response, ex, HttpStatus.UNAUTHORIZED, CoreError.ACCESS_ERROR);
        }
    }

    private void validateGatewayHeader(HttpServletRequest request) {
        String gatewayKey = request.getHeader("Gateway");

        if (gatewayKey == null || !gatewayKey.equals(gatewaySecretKey)) {
            throw new AccessException("Use Gateway entrance");
        }
    }

    private void handleException(HttpServletResponse response, Exception ex, HttpStatus status, CoreError error) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse()
                .code(error.getCode())
                .message(ex.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private boolean isNotAuthenticated(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void authenticateUser(HttpServletRequest request, String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
