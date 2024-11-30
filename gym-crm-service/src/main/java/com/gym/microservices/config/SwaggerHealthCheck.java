package com.gym.microservices.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SwaggerHealthCheck implements HealthIndicator {

    private final RestTemplate restTemplate;

    @Value("${app.root.url}")
    private String rootUrl;

    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;

    public SwaggerHealthCheck(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            String url = rootUrl + swaggerPath;

            String response = restTemplate.getForObject(url, String.class);
            if (response != null) {
                return Health.up().withDetail("Swagger API", "Available").build();
            }
        } catch (Exception e) {
            return Health.down().withDetail("Swagger API", "Unavailable").build();
        }
        return Health.down().build();
    }
}

