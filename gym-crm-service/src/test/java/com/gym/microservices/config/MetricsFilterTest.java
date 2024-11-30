package com.gym.microservices.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricsFilterTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter serverErrorCounter;

    @Mock
    private Counter incorrectLoginCounter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    private MetricsFilter metricsFilter;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter(eq("server_errors_total"), eq("type"), eq("5xx"))).thenReturn(serverErrorCounter);
        when(meterRegistry.counter(eq("incorrect_login_total"), eq("type"), eq("detail"))).thenReturn(incorrectLoginCounter);
        metricsFilter = new MetricsFilter(meterRegistry);
    }

    @Test
    void checkIfServerErrorCounterIs() throws IOException, ServletException {
        when(httpServletResponse.getStatus()).thenReturn(500);
        when(httpServletRequest.getRequestURI()).thenReturn("\"/some/other/path\"");

        metricsFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(serverErrorCounter, times(1)).increment();
    }

    @Test
    void checkIfIncorrectLoginCounterIs() throws IOException, ServletException {
        when(httpServletResponse.getStatus()).thenReturn(401);
        when(httpServletRequest.getRequestURI()).thenReturn("/trainee/login");

        metricsFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(incorrectLoginCounter, times(1)).increment();
    }

    @Test
    void checkIfNothingHappens() throws IOException, ServletException {
        when(httpServletResponse.getStatus()).thenReturn(200);

        metricsFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(serverErrorCounter, never()).increment();
        verify(incorrectLoginCounter, never()).increment();
    }
}