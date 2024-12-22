package com.gym.analytics.controller.error;

import com.gym.analytics.dto.ErrorResponse;
import com.gym.analytics.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @InjectMocks
    private final ErrorHandler controllerAdvice = new ErrorHandler();

    @Test
    void TestHandleHandlerMethodValidationException() {
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);
        MessageSourceResolvable error1 = mock(MessageSourceResolvable.class);

        when(error1.getDefaultMessage()).thenReturn("Username must not be blank");

        List<MessageSourceResolvable> errors = List.of(error1);
        Mockito.<List<? extends MessageSourceResolvable>>when(exception.getAllErrors()).thenReturn(errors);

        ResponseEntity<Object> response = controllerAdvice.handleHandlerMethodValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof List<?>);

        List<?> responseBody = (List<?>) response.getBody();
        assertEquals(1, responseBody.size());
        assertTrue(responseBody.contains("Username must not be blank"));
    }

    @Test
    void testHandleValidationExceptions() {
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        List<FieldError> fieldErrors = List.of(
                new FieldError("TrainerWorkloadRequest", "username", "Username is invalid"),
                new FieldError("TrainerWorkloadRequest", "trainingDuration", "Training duration must not be zero")
        );

        Mockito.when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        Mockito.when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = controllerAdvice.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals("Username is invalid", responseBody.get("username"));
        assertEquals("Training duration must not be zero", responseBody.get("trainingDuration"));
    }

    @Test
    void testHandleEntityNotFoundException() {
        String errorMessage = "Something went wrong";
        String errorCode = "710";
        EntityNotFoundException exception = new EntityNotFoundException(errorMessage);

        ResponseEntity<Object> responseEntity = controllerAdvice.handleEntityNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();

        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(errorCode, errorResponse.getCode());
    }

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