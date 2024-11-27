package com.gym.microservices.exception;

import lombok.Getter;

@Getter
public class AccessException extends RuntimeException {

    public AccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessException(String message) {
        super(message);
    }
}
