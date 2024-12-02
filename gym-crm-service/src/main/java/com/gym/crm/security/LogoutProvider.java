package com.gym.crm.security;

import com.gym.crm.service.impl.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class LogoutProvider {

    private final TokenBlacklistService blacklistService;

    public void execute(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            blacklistService.blacklistToken(token);
        }

        SecurityContextHolder.clearContext();
    }
}
