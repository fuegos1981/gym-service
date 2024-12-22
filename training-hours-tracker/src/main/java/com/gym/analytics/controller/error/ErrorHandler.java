package com.gym.analytics.controller.error;

import com.gym.analytics.dto.ErrorResponse;
import com.gym.analytics.exception.CoreError;
import com.gym.analytics.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gym.analytics.exception.CoreError.ENTITY_NOT_FOUND_ERROR;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        List<String> errors = new ArrayList<>();
        ex.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        if (ex.getCause() instanceof SocketTimeoutException) {
            return handleTimeoutException((SocketTimeoutException) ex.getCause());
        }

        ErrorResponse errorResponse = constructErrorResponse(ENTITY_NOT_FOUND_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

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