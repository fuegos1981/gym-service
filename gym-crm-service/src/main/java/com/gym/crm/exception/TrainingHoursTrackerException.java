package com.gym.crm.exception;

import lombok.Getter;

@Getter
public class TrainingHoursTrackerException extends RuntimeException {

    public TrainingHoursTrackerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrainingHoursTrackerException(String message) {
        super(message);
    }
}
