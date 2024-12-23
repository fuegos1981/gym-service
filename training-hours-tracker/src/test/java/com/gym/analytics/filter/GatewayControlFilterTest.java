package com.gym.analytics.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    private final String VALID_GATEWAY_KEY = "validKey";

    private GatewayControlFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Mock
    private FilterChain mockFilterChain;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        filter = new GatewayControlFilter();

        Field gatewaySecretKeyField = GatewayControlFilter.class.getDeclaredField("gatewaySecretKey");
        gatewaySecretKeyField.setAccessible(true);
        gatewaySecretKeyField.set(filter, VALID_GATEWAY_KEY);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void checkIfFilterWithValidGatewayHeader() throws ServletException, IOException {
        request.addHeader("Gateway", VALID_GATEWAY_KEY);

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

        String expectedResponse = constructErrorResponse();

        assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    void checkIfFilterWithoutGatewayHeader() throws ServletException, IOException {
        filter.doFilterInternal(request, response, mockFilterChain);

        verify(mockFilterChain, times(0)).doFilter(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        String expectedResponse = constructErrorResponse();

        assertEquals(expectedResponse, response.getContentAsString());
    }

    private String constructErrorResponse() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(
                new ErrorResponse()
                        .code(CoreError.ACCESS_ERROR.getCode())
                        .message("Use Gateway entrance")
        );
    }
}