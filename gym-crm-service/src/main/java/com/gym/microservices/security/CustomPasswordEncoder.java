package com.gym.microservices.security;

import com.gym.microservices.exception.ServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return generatePasswordHash(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return generatePasswordHash(rawPassword.toString()).equals(encodedPassword);
    }

    public String generatePasswordHash(String password) {
        byte[] specialSalt = "specialSalt".getBytes();
        KeySpec spec = new PBEKeySpec(password.toCharArray(), specialSalt, 1024, 128);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();

            return IntStream.range(0, hash.length)
                    .mapToObj(i -> String.format("%02X", hash[i]))
                    .collect(Collectors.joining());

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ServiceException("Cannot generate password hash" + e.getMessage(), e);
        }
    }
}
