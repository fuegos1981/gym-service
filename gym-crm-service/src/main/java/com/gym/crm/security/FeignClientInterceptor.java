package com.gym.crm.security;

import com.gym.crm.constants.GlobalConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private final JwtProvider jwtProvider;

    @Value("${app.analytic_username}")
    private String username;
    @Value("${app.analytic_password}")
    private String password;

    public FeignClientInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = getJwtToken();
        requestTemplate.header("Authorization", token);
        requestTemplate.header(GlobalConstants.TRANSACTION_ID, MDC.get(GlobalConstants.TRANSACTION_ID));
    }

    private String getJwtToken() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(password)
                .build();

        String token = "Bearer " + jwtProvider.generateToken(userDetails);
        log.info("Token for analytics: [transactionId={}, token={}]", MDC.get(GlobalConstants.TRANSACTION_ID), token);
        return token;
    }
}
