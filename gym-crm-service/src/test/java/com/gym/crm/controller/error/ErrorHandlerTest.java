package com.gym.crm.controller.error;

import com.gym.crm.dto.ErrorResponse;
import com.gym.crm.exception.AccessException;
import com.gym.crm.exception.DatabaseException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.RepositoryException;
import com.gym.crm.exception.ServiceException;
import com.gym.crm.exception.TrainingHoursTrackerException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {

    private final ErrorHandler controllerAdvice = new ErrorHandler();

    @Test
    void handleEntityNotFoundException() throws SocketTimeoutException {
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
    void handleAccessException() throws SocketTimeoutException {
        String errorMessage = "Something went wrong";
        String errorCode = "535";
        AccessException exception = new AccessException(errorMessage);

        ResponseEntity<Object> responseEntity = controllerAdvice.handleAccessException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();

        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(errorCode, errorResponse.getCode());
    }

    @Test
    void handleRepositoryException() throws SocketTimeoutException {
        String errorMessage = "Something went wrong";
        String errorCode = "853";
        RepositoryException exception = new RepositoryException(errorMessage);

        ResponseEntity<Object> responseEntity = controllerAdvice.handleRepositoryException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();

        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(errorCode, errorResponse.getCode());
    }

    @Test
    void handleServiceException() throws SocketTimeoutException {
        String errorMessage = "Something went wrong";
        String errorCode = "900";
        ServiceException exception = new ServiceException(errorMessage);

        ResponseEntity<Object> responseEntity = controllerAdvice.handleServiceException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();

        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(errorCode, errorResponse.getCode());
    }

    @Test
    void testHandleServerException() throws SocketTimeoutException {
        String errorMessage = "Something went wrong";
        String errorCode = "930";
        DatabaseException databaseException = new DatabaseException(errorMessage);

        ResponseEntity<Object> responseEntity = controllerAdvice.handleServerException(databaseException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();

        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(errorCode, errorResponse.getCode());
    }

    @Test
    void testHandleTrainingHoursTrackerException() throws SocketTimeoutException {
        String errorMessage = "Something went wrong";
        String errorCode = "930";
        TrainingHoursTrackerException trackerException = new TrainingHoursTrackerException(errorMessage);

        ResponseEntity<Object> responseEntity = controllerAdvice.handleTrainingHoursTrackerException(trackerException);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();

        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(errorCode, errorResponse.getCode());
    }

    @Test
    void testHandleRuntimeException() throws SocketTimeoutException {
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

    @Test
    public void handleTrainingHoursTrackerException_Timeout_ShouldReturn504() throws SocketTimeoutException {
        SocketTimeoutException timeoutException = new SocketTimeoutException("Simulated timeout");
        TrainingHoursTrackerException ex = new TrainingHoursTrackerException("Timeout occurred", timeoutException);

        ResponseEntity<Object> response = controllerAdvice.handleTrainingHoursTrackerException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
        assertEquals("Request timed out. Please try again later.", ((ErrorResponse) response.getBody()).getMessage());
    }
}