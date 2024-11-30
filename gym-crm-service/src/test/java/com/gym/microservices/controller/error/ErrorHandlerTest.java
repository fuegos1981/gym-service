package com.gym.microservices.controller.error;

import com.gym.microservices.dto.ErrorResponse;
import com.gym.microservices.exception.AccessException;
import com.gym.microservices.exception.DatabaseException;
import com.gym.microservices.exception.EntityNotFoundException;
import com.gym.microservices.exception.RepositoryException;
import com.gym.microservices.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    private final ErrorHandler controllerAdvice = new ErrorHandler();

    @Test
    void handleEntityNotFoundException() {
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
    void handleAccessException() {
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
    void handleRepositoryException() {
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
    void handleServiceException() {
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
    void testHandleServerException() {
        String errorMessage = "Something went wrong";
        String errorCode = "930";
        DatabaseException databaseException = new DatabaseException(errorMessage);

        ResponseEntity<Object> responseEntity = controllerAdvice.handleServerException(databaseException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();

        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(errorCode, errorResponse.getCode());
    }
}