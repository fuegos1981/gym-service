package com.gym.crm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BruteForceProtectorTest {

    public static final String USERNAME = "testUser";

    private BruteForceProtector bruteForceProtector;

    @BeforeEach
    void setUp() {
        bruteForceProtector = new BruteForceProtector();
    }

    @Test
    void checkIfLoginFailedIsWorked() {
        bruteForceProtector.loginFailed(USERNAME);
        assertFalse(bruteForceProtector.isBlocked(USERNAME));

        bruteForceProtector.loginFailed(USERNAME);
        assertFalse(bruteForceProtector.isBlocked(USERNAME));

        bruteForceProtector.loginFailed(USERNAME);
        assertTrue(bruteForceProtector.isBlocked(USERNAME));
    }

    @Test
    void checkIfLoginSucceededIsWorked() {
        IntStream.range(0, 3).forEach(i -> bruteForceProtector.loginFailed(USERNAME));

        assertTrue(bruteForceProtector.isBlocked(USERNAME));

        bruteForceProtector.loginSucceeded(USERNAME);

        assertFalse(bruteForceProtector.isBlocked(USERNAME));
    }

    @Test
    void checkIfIsBlockedIsWorked() {
        IntStream.range(0, 3).forEach(i -> bruteForceProtector.loginFailed(USERNAME));
        assertTrue(bruteForceProtector.isBlocked(USERNAME));
    }
}