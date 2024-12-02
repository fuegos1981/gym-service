package com.gym.crm.security;

import com.gym.crm.service.impl.CustomUserDetailsService;
import com.gym.crm.service.impl.TokenBlacklistService;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = getUsernameFromToken(jwt);
        }

        if (isNotAuthenticated(username)) {
            authenticateUser(request, username, jwt);
        }

        chain.doFilter(request, response);
    }

    private String getUsernameFromToken(String jwt) {
        String username = null;

        if (!blacklistService.isTokenBlacklisted(jwt)) {
            username = extractUsernameFromToken(jwt);
        }

        return username;
    }

    private String extractUsernameFromToken(String jwt) {
        try {
            return jwtProvider.extractUsername(jwt);
        } catch (SignatureException e) {
            log.warn("invalid token");
            return null;
        }
    }

    private boolean isNotAuthenticated(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void authenticateUser(HttpServletRequest request, String username, String jwt) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtProvider.validateToken(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}
