package com.gym.crm.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataBaseHealthCheck implements HealthIndicator {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        boolean databaseUp = checkDatabaseConnection();
        if (databaseUp) {
            return Health.up().withDetail("database", "is working").build();
        } else {
            return Health.down().withDetail("Error", "Database is down").build();
        }
    }

    private boolean checkDatabaseConnection() {
        try {
            jdbcTemplate.execute("select 1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
