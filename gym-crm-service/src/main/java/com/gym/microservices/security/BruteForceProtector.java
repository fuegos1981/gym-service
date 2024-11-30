package com.gym.microservices.security;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BruteForceProtector {
    private static final int MAX_ATTEMPTS = 3;
    private static final int BLOCK_DURATION_MINUTES = 5;

    private final Map<String, LoginAttempt> attemptsCache = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        LoginAttempt loginAttempt = attemptsCache.getOrDefault(username, new LoginAttempt());
        loginAttempt.incrementCount();

        if (loginAttempt.getCount() >= MAX_ATTEMPTS) {
            loginAttempt.setBlockTime(LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES));
        }

        attemptsCache.put(username, loginAttempt);
    }

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
    }

    public boolean isBlocked(String username) {
        LoginAttempt loginAttempt = attemptsCache.get(username);
        return loginAttempt != null && loginAttempt.isBlocked();
    }
}
