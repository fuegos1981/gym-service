package com.gym.crm.automation.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GlobalSender {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${gateway.secret}")
    String gatewayKey;

    protected HttpHeaders createHeadersWithAuth(boolean hasToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Gateway", gatewayKey);
        headers.add("X-USER", "Test.User");
        if (hasToken) {
            headers.add("Authorization", "Bearer trtrttrtrtr");
        }

        return headers;
    }

    protected <T> ResponseEntity<T> post(String url, Object body, Class<T> responseType) {
        HttpEntity<Object> entity = new HttpEntity<>(body, createHeadersWithAuth(false));
        return restTemplate.postForEntity(url, entity, responseType);
    }

    protected <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        HttpHeaders headers = createHeadersWithAuth(true);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }

    protected <T> ResponseEntity<T> put(String url, Object body, Class<T> responseType) {
        HttpHeaders headers = createHeadersWithAuth(true);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
    }

    protected void delete(String url) {
        HttpHeaders headers = createHeadersWithAuth(true);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }
}
