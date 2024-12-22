package com.gym.analytics.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.analytics.dto.ErrorResponse;
import com.gym.analytics.exception.CoreError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GatewayControlFilterTest {

    private GatewayControlFilter filter;

    @Mock
    private FilterChain mockFilterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private final String validGatewayKey = "validKey";

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        filter = new GatewayControlFilter();

        Field gatewaySecretKeyField = GatewayControlFilter.class.getDeclaredField("gatewaySecretKey");
        gatewaySecretKeyField.setAccessible(true);
        gatewaySecretKeyField.set(filter, validGatewayKey);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void checkIfFilterWithValidGatewayHeader() throws ServletException, IOException {
        request.addHeader("Gateway", validGatewayKey);

        filter.doFilterInternal(request, response, mockFilterChain);

        verify(mockFilterChain, times(1)).doFilter(request, response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void checkIfFilterWithInvalidGatewayHeader() throws ServletException, IOException {
        request.addHeader("Gateway", "invalidKey");

        filter.doFilterInternal(request, response, mockFilterChain);

        verify(mockFilterChain, times(0)).doFilter(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedResponse = objectMapper.writeValueAsString(
                new ErrorResponse()
                        .code(CoreError.ACCESS_ERROR.getCode())
                        .message("Without Gateway!")
        );
        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void checkIfFilterWithoutGatewayHeader() throws ServletException, IOException {
        filter.doFilterInternal(request, response, mockFilterChain);

        verify(mockFilterChain, times(0)).doFilter(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedResponse = objectMapper.writeValueAsString(
                new ErrorResponse()
                        .code(CoreError.ACCESS_ERROR.getCode())
                        .message("Without Gateway!")
        );
        assertEquals(expectedResponse, response.getContentAsString());
    }

}