package com.gym.analytics.exception;

public enum CoreError {
    GENERAL_ERROR("500"),
    ACCESS_ERROR("535"),
    ENTITY_NOT_FOUND_ERROR("710"),
    REPOSITORY_ERROR("853"),
    SERVER_ERROR("930"),
    SERVICE_ERROR("900"),
    TIMEOUT_ERROR("945");

    private final String code;

    CoreError(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
