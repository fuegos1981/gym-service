package com.gym.analytics.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.analytics.dto.ErrorResponse;
import com.gym.analytics.exception.AccessException;
import com.gym.analytics.exception.CoreError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class GatewayControlFilter extends OncePerRequestFilter {

    @Value("${gateway.secret}")
    private String gatewaySecretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            validateGatewayHeader(request);
            chain.doFilter(request, response);
        } catch (AccessException ex) {
            handleException(response, ex, HttpStatus.UNAUTHORIZED, CoreError.ACCESS_ERROR);
        }
    }

    private void validateGatewayHeader(HttpServletRequest request) {
        String gatewayKey = request.getHeader("Gateway");

        if (gatewayKey == null || !gatewayKey.equals(gatewaySecretKey)) {
            throw new AccessException("Without Gateway!");
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
}
