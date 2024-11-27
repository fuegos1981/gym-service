package com.gym.microservices.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoggingFilterTest {

    private LoggingFilter loggingFilter;
    private FilterChain mockFilterChain;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private LoggingFilter.ResettableStreamHttpServletRequest wrappedRequest;

    @BeforeEach
    public void setUp() {
        loggingFilter = new LoggingFilter();
        mockFilterChain = mock(FilterChain.class);
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        wrappedRequest = new LoggingFilter.ResettableStreamHttpServletRequest(mockRequest);
    }

    @Test
    public void testLogRequest_MasksPassword() throws IOException {
        String requestBody = "username=user&password=secret123";
        mockRequest.setContent(requestBody.getBytes(StandardCharsets.UTF_8)); // Устанавливаем тело запроса

        loggingFilter.logRequest(wrappedRequest, UUID.randomUUID().toString(), true);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        assertTrue(loggingFilter.hidePassword(requestBody).contains("password=**********"));
    }

    @Test
    public void testLogResponse_StatusOK() {
        ContentCachingResponseWrapper mockResp = mock(ContentCachingResponseWrapper.class);
        when(mockResp.getStatus()).thenReturn(HttpStatus.OK.value()); // Simulate a successful status
        when(mockResp.getContentAsByteArray()).thenReturn("success".getBytes(StandardCharsets.UTF_8));

        loggingFilter.logResponse(mockResp, UUID.randomUUID().toString(), true);

        String responseBody = new String(mockResp.getContentAsByteArray(), StandardCharsets.UTF_8);
        assertEquals("success", responseBody);
    }

    @Test
    public void testHidePassword() {
        String bodyWithPassword = "username=user&password=mySecretPassword123";
        String hiddenBody = loggingFilter.hidePassword(bodyWithPassword);

        assertTrue(hiddenBody.contains("password=**********"));
        assertFalse(hiddenBody.contains("mySecretPassword123"));
    }

    @Test
    public void testDoFilter_ClearsMDC() throws IOException, ServletException {
        String transactionId = UUID.randomUUID().toString();
        mockRequest.addHeader("X-Transaction-Id", transactionId);

        loggingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertNull(MDC.get("transactionId"));
    }
}