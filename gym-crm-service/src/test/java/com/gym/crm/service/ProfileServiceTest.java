package com.gym.crm.service;

import com.gym.crm.service.impl.ProfileService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileServiceTest {

    private static final long PASSWORD_AMOUNT = 1000;
    private static final String USERNAME = "Ivan.Petrov";
    private static final ProfileService service = new ProfileService();

    @Test
    void checkIfGetPasswordIsCorrect() {
        long validPasswords = LongStream.range(0, PASSWORD_AMOUNT)
                .mapToObj(i -> service.createNewPassword())
                .filter(password -> password.length() == 10)
                .filter(password -> !password.matches(".*\\p{Punct}.*"))
                .count();

        assertEquals(PASSWORD_AMOUNT, validPasswords);
    }

    @Test
    void checkIfNewUsernameNotHaveSimilar() {
        List<String> duplicateNames = Collections.emptyList();

        String username = service.buildUsername(USERNAME, duplicateNames);

        assertEquals(USERNAME, username);
    }

    @Test
    void checkIfNewUsernameHaveOneSimilar() {
        List<String> duplicateNames = List.of(USERNAME);

        String username = service.buildUsername(USERNAME, duplicateNames);

        assertEquals(USERNAME + "1", username);
    }

    @Test
    void checkIfNewUsernameHaveThreeSimilar() {
        List<String> duplicateNames = List.of(USERNAME, USERNAME + "1", USERNAME + "2");

        String username = service.buildUsername(USERNAME, duplicateNames);

        assertEquals(USERNAME + "3", username);
    }
}