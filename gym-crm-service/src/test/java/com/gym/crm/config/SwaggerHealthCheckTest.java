package com.gym.crm.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerHealthCheckTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SwaggerHealthCheck swaggerHealthCheck;

    private final String SWAGGER_URL = "http://localhost:8080/api/v1/swagger-ui.html";

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(swaggerHealthCheck, "rootUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(swaggerHealthCheck, "swaggerPath", "/api/v1/swagger-ui.html");
    }

    @Test
    void checkIfSwaggerIsUp() {
        when(restTemplate.getForObject(SWAGGER_URL, String.class)).thenReturn("Swagger UI Response");
        Health health = swaggerHealthCheck.health();
        assertEquals(Health.up().withDetail("Swagger API", "Available").build(), health);
    }

    @Test
    void checkIfSwaggerIsDown() {
        when(restTemplate.getForObject(SWAGGER_URL, String.class)).thenThrow(new RuntimeException("Service Unavailable"));
        Health health = swaggerHealthCheck.health();
        assertEquals(Health.down().withDetail("Swagger API", "Unavailable").build(), health);
    }
}