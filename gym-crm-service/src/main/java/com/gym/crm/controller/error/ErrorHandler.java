package com.gym.crm.controller.error;

import com.gym.crm.dto.ErrorResponse;
import com.gym.crm.exception.AccessException;
import com.gym.crm.exception.CoreError;
import com.gym.crm.exception.DatabaseException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.RepositoryException;
import com.gym.crm.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.gym.crm.exception.CoreError.ACCESS_ERROR;
import static com.gym.crm.exception.CoreError.ENTITY_NOT_FOUND_ERROR;
import static com.gym.crm.exception.CoreError.SERVER_ERROR;
import static com.gym.crm.exception.CoreError.SERVICE_ERROR;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse errorResponse = constructErrorResponse(ENTITY_NOT_FOUND_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessException.class)
    public ResponseEntity<Object> handleAccessException(AccessException ex) {
        ErrorResponse errorResponse = constructErrorResponse(ACCESS_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<Object> handleRepositoryException(RepositoryException ex) {
        ErrorResponse errorResponse = constructErrorResponse(CoreError.REPOSITORY_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleServiceException(ServiceException ex) {
        ErrorResponse errorResponse = constructErrorResponse(SERVICE_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Object> handleServerException(DatabaseException ex) {
        ErrorResponse errorResponse = constructErrorResponse(SERVER_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse constructErrorResponse(CoreError coreError, RuntimeException exception) {
        return new ErrorResponse().code(coreError.getCode()).message(exception.getMessage());
    }
}