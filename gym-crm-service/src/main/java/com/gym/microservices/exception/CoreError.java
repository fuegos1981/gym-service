package com.gym.microservices.exception;

public enum CoreError {
    GENERAL_ERROR("500"),
    ACCESS_ERROR("535"),
    ENTITY_NOT_FOUND_ERROR("710"),
    REPOSITORY_ERROR("853"),
    SERVER_ERROR("930"),
    SERVICE_ERROR("900");

    private final String code;

    CoreError(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
