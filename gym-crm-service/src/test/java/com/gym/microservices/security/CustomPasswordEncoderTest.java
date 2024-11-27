package com.gym.microservices.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomPasswordEncoderTest {

    private static final CharSequence PASSWORD = "1111111111";
    private static final String EXPECTED = "30C69C984072631B8146228C3051CA2A";

    private final CustomPasswordEncoder passwordEncoder = new CustomPasswordEncoder();

    @Test
    void checkIfEncodeIsWorked() {
        String actual = passwordEncoder.encode(PASSWORD);
        assertEquals(EXPECTED, actual);
    }

    @Test
    void checkIfMatchesIsWorked() {
        boolean actual = passwordEncoder.matches(PASSWORD, EXPECTED);
        assertTrue(actual);
    }

    @Test
    void checkIfGeneratePasswordHashSaltIsCorrect() {
        String actual = passwordEncoder.generatePasswordHash(PASSWORD.toString());
        assertEquals(EXPECTED, actual);
    }
}