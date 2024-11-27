package com.gym.microservices.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MetricsFilter implements Filter {

    private final Counter serverErrorCounter;

    private final Counter incorrectLoginCounter;

    public MetricsFilter(MeterRegistry meterRegistry) {
        this.serverErrorCounter = meterRegistry.counter("server_errors_total", "type", "5xx");
        this.incorrectLoginCounter = meterRegistry.counter("incorrect_login_total", "type", "detail");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        chain.doFilter(request, response);

        int status = httpServletResponse.getStatus();

        if (status >= 500 && status < 600) {
            serverErrorCounter.increment();
        }

        if (hasIncorrectLogin(httpServletRequest, status)) {
            incorrectLoginCounter.increment();
        }
    }

    private boolean hasIncorrectLogin(HttpServletRequest httpServletRequest, int status) {
        return status >= 400 && status < 600 && httpServletRequest.getRequestURI().contains("/login");
    }
}
