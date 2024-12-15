package com.gym.app.constants;

import java.util.Set;

public class GlobalConstants {

    public static final Set<String> LOGOUT_PATH = Set.of(
            "/api/v1/gym-crm-service/trainee/logout",
            "/api/v1/gym-crm-service/trainer/logout"
    );

    public static final Set<String> PERMIT_ALL_URLS = Set.of(
            "/api/v1/gym-crm-service/v3/api-docs/**",
            "/api/v1/gym-crm-service/swagger-ui/**",
            "/api/v1/gym-crm-service/trainee/register",
            "/api/v1/gym-crm-service/trainee/login",
            "/api/v1/gym-crm-service/trainer/login",
            "/api/v1/gym-crm-service/trainer/register",
            "/api/v1/gym-crm-service/swagger-ui.html"
    );
}
