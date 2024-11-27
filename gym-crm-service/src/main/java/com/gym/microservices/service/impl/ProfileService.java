package com.gym.microservices.service.impl;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@NoArgsConstructor
public final class ProfileService {
    private static final int PASSWORD_LENGTH = 10;

    public String createNewPassword() {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(this::isAlphanumeric)
                .limit(PASSWORD_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String buildUsername(String username, List<String> duplicateNames) {
        if (duplicateNames.isEmpty()) {
            return username;
        }

        int nameSuffix = duplicateNames.size();

        return username + nameSuffix;
    }

    private boolean isAlphanumeric(int i) {
        return (i <= 57 || i >= 65) && (i <= 90 || i >= 97);
    }
}
