package com.gym.microservices.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class DataBaseHealthCheckTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DataBaseHealthCheck dataBaseHealthCheck;

    @Test
    void checkIfDatabaseIsUp() {
        doNothing().when(jdbcTemplate).execute("select 1");
        Health health = dataBaseHealthCheck.health();
        assertEquals(Health.up().withDetail("database", "is working").build(), health);
    }

    @Test
    void checkIfDatabaseIsDown() {
        doThrow(new RuntimeException("Database is down")).when(jdbcTemplate).execute("select 1");
        Health health = dataBaseHealthCheck.health();
        assertEquals(Health.down().withDetail("Error", "Database is down").build(), health);
    }
}