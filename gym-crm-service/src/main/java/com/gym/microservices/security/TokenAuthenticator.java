package com.gym.microservices.security;

import com.gym.microservices.exception.AccessException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TokenAuthenticator {

    private final JwtProvider jwtProvider;

    private final CustomAuthenticationProvider authenticationProvider;

    public String authenticate(String username, String password) {
        try {
            Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return jwtProvider.generateToken(userDetails);
        } catch (BadCredentialsException e) {
            throw new AccessException("Invalid credentials", e);
        }
    }
}
