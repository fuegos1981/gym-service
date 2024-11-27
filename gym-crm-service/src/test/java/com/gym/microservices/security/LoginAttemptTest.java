package com.gym.microservices.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptTest {

    private static final LocalDateTime BLOCK_TIME = LocalDateTime.now().plusMinutes(5);

    private LoginAttempt loginAttempt;

    @BeforeEach
    public void setUp() {
        loginAttempt = new LoginAttempt();
    }

    @Test
    void setBlockTime() {
        loginAttempt.setBlockTime(BLOCK_TIME);

        assertEquals(BLOCK_TIME, loginAttempt.getBlockTime());
        assertEquals(0, loginAttempt.getCount());
    }

    @Test
    void incrementCount() {
        assertEquals(0, loginAttempt.getCount());
        loginAttempt.incrementCount();
        assertEquals(1, loginAttempt.getCount());
        loginAttempt.incrementCount();
        assertEquals(2, loginAttempt.getCount());
    }

    @Test
    void isBlocked() {
        loginAttempt.setBlockTime(BLOCK_TIME);
        assertTrue(loginAttempt.isBlocked());
    }
}