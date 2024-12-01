package com.gym.microservices.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Value("${app.analytic_username}")
    private String username;
    @Value("${app.analytic_password}")
    private String password;

    private final JwtProvider jwtProvider;

    public FeignClientInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    private String getJwtToken() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(password)
                .build();

        String token = "Bearer " + jwtProvider.generateToken(userDetails);
        log.info("Token for analytics: " + token);
        return token;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = getJwtToken();
        requestTemplate.header("Authorization", token);
    }
}
