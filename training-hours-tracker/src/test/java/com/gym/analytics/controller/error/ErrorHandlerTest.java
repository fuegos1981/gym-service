package com.gym.analytics.controller.error;

import com.gym.analytics.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    private final ErrorHandler controllerAdvice = new ErrorHandler();

    @Test
    void testHandleRuntimeException() {
        String errorMessage = "Something went wrong";
        String errorCode = "500";
        RuntimeException runtimeException = new RuntimeException(errorMessage);

        ResponseEntity<Object> responseEntity = controllerAdvice.handleRuntimeException(runtimeException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();

        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(errorCode, errorResponse.getCode());
    }

    @Test
    void testHandleSocketTimeoutException() {
        String errorMessage = "Request timed out. Please try again later.";
        String errorCode = "945";
        SocketTimeoutException timeoutException = new SocketTimeoutException(errorMessage);

        ResponseEntity<Object> responseEntity = controllerAdvice.handleTimeoutException(timeoutException);

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();

        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(errorCode, errorResponse.getCode());
    }
}