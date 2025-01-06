package com.gym.analytics.automation.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.analytics.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {AppConfig.class})
public class GlobalSender {

    private final static String HEADER_GATEWAY = "Gateway";
    private final static String HEADER_USER = "X-USER";
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String BEARER_TOKEN = "Bearer trtrttrtrtr";
    private final static String USERNAME = "John.Doe";

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${gateway.secret}")
    String gatewayKey;

    protected HttpHeaders createHeadersWithAuth(boolean hasGateway) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_USER, USERNAME);
        headers.add(HEADER_AUTHORIZATION, BEARER_TOKEN);

        if (hasGateway) {
            headers.add(HEADER_GATEWAY, gatewayKey);
        }

        return headers;
    }

    protected <T> ResponseEntity<T> post(String url, Object body, Class<T> responseType) {
        HttpEntity<Object> entity = new HttpEntity<>(body, createHeadersWithAuth(true));
        return restTemplate.postForEntity(url, entity, responseType);
    }

    protected <T> ResponseEntity<T> get(String url, Class<T> responseType, boolean hasGateway) {
        HttpHeaders headers = createHeadersWithAuth(hasGateway);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }
}
