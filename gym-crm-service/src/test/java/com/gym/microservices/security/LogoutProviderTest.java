package com.gym.microservices.security;

import com.gym.microservices.service.impl.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutProviderTest {

    @Mock
    private TokenBlacklistService blacklistService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LogoutProvider logoutProvider;

    @Test
    void checkIfExecuteIsWorked() {
        String token = "testToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        logoutProvider.execute(request);

        verify(blacklistService, times(1)).blacklistToken(token);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void checkIfExecuteWithoutToken() {
        when(request.getHeader("Authorization")).thenReturn(null);

        logoutProvider.execute(request);

        verify(blacklistService, never()).blacklistToken(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}