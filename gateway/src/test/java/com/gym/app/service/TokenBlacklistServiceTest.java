package com.gym.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBlacklistServiceTest {

    private static final String TOKEN = "jwtToken";

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    void checkIfTokenAddToBlacklist() {
        tokenBlacklistService.blacklistToken(TOKEN);
        assertTrue(tokenBlacklistService.isTokenBlacklisted(TOKEN));
    }

    @Test
    void checkIfTokenNotInBlacklist() {
        assertFalse(tokenBlacklistService.isTokenBlacklisted(TOKEN));
    }

}