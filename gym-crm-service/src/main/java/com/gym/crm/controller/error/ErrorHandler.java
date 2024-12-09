package com.gym.crm.controller.error;

import com.gym.crm.dto.ErrorResponse;
import com.gym.crm.exception.AccessException;
import com.gym.crm.exception.CoreError;
import com.gym.crm.exception.DatabaseException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.RepositoryException;
import com.gym.crm.exception.ServiceException;
import com.gym.crm.exception.TrainingHoursTrackerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketTimeoutException;

import static com.gym.crm.exception.CoreError.ACCESS_ERROR;
import static com.gym.crm.exception.CoreError.ENTITY_NOT_FOUND_ERROR;
import static com.gym.crm.exception.CoreError.SERVER_ERROR;
import static com.gym.crm.exception.CoreError.SERVICE_ERROR;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) throws SocketTimeoutException {
        if (ex.getCause() instanceof SocketTimeoutException) {
            return handleTimeoutException((SocketTimeoutException) ex.getCause());
        }

        ErrorResponse errorResponse = constructErrorResponse(ENTITY_NOT_FOUND_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessException.class)
    public ResponseEntity<Object> handleAccessException(AccessException ex) throws SocketTimeoutException {
        if (ex.getCause() instanceof SocketTimeoutException) {
            return handleTimeoutException((SocketTimeoutException) ex.getCause());
        }

        ErrorResponse errorResponse = constructErrorResponse(ACCESS_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<Object> handleRepositoryException(RepositoryException ex) throws SocketTimeoutException {
        if (ex.getCause() instanceof SocketTimeoutException) {
            return handleTimeoutException((SocketTimeoutException) ex.getCause());
        }

        ErrorResponse errorResponse = constructErrorResponse(CoreError.REPOSITORY_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleServiceException(ServiceException ex) throws SocketTimeoutException {
        if (ex.getCause() instanceof SocketTimeoutException) {
            return handleTimeoutException((SocketTimeoutException) ex.getCause());
        }

        ErrorResponse errorResponse = constructErrorResponse(SERVICE_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Object> handleServerException(DatabaseException ex) throws SocketTimeoutException {
        if (ex.getCause() instanceof SocketTimeoutException) {
            return handleTimeoutException((SocketTimeoutException) ex.getCause());
        }

        ErrorResponse errorResponse = constructErrorResponse(SERVER_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TrainingHoursTrackerException.class)
    public ResponseEntity<Object> handleTrainingHoursTrackerException(TrainingHoursTrackerException ex) throws SocketTimeoutException {
        if (ex.getCause() instanceof SocketTimeoutException) {
            return handleTimeoutException((SocketTimeoutException) ex.getCause());
        }

        ErrorResponse errorResponse = constructErrorResponse(SERVER_ERROR, ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) throws SocketTimeoutException {
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