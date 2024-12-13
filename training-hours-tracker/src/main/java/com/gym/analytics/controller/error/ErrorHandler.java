package com.gym.analytics.controller.error;

import com.gym.analytics.dto.ErrorResponse;
import com.gym.analytics.exception.CoreError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketTimeoutException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        if (ex.getCause() instanceof SocketTimeoutException) {
            return handleTimeoutException((SocketTimeoutException) ex.getCause());
        }

        ErrorResponse errorResponse = constructErrorResponse(CoreError.GENERAL_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<Object> handleTimeoutException(SocketTimeoutException ex) {
        ErrorResponse errorResponse = new ErrorResponse()
                .code(CoreError.TIMEOUT_ERROR.getCode())
                .message("Request timed out. Please try again later.");
        return new ResponseEntity<>(errorResponse, HttpStatus.GATEWAY_TIMEOUT);
    }

    private ErrorResponse constructErrorResponse(CoreError coreError, RuntimeException exception) {
        return new ErrorResponse().code(coreError.getCode()).message(exception.getMessage());
    }
}