package com.gym.analytics.exception;

import lombok.Getter;

@Getter
public class AccessException extends RuntimeException {

    public AccessException(String message) {
        super(message);
    }
}
