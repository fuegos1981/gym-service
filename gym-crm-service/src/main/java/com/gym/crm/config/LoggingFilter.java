package com.gym.crm.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        String transactionId = wrappedRequest.getHeader("X-Transaction-Id");
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }
        MDC.put("transactionId", transactionId);
        boolean hasBody = checkUrl(wrappedRequest.getRequestURI());

        logRequest(wrappedRequest, transactionId, hasBody);

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logResponse(wrappedResponse, transactionId, hasBody);
            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }

    public void logRequest(ResettableStreamHttpServletRequest request, String transactionId, boolean hasBody) throws IOException {
        String requestBody;

        if (!hasBody) {
            requestBody = "";
        } else if (request.getQueryString() == null) {
            requestBody = hidePassword(IOUtils.toString(request.getReader()));
            request.resetInputStream();
        } else {
            requestBody = hidePassword(request.getQueryString());
        }

        log.info("Incoming Request: [transactionId={}, method={}, URI={}, body={}]",
                transactionId,
                request.getMethod(),
                request.getRequestURI(),
                requestBody.isEmpty() ? "No Body" : requestBody);
    }

    public String hidePassword(String body) {
        return body.replaceAll("(?i)(password=)[^&]+", "$1**********")
                .replaceAll("(?i)(Password\"\\s*:\\s*\")[^\"]+\"", "$1**********\"");
    }

    public void logResponse(ContentCachingResponseWrapper response, String transactionId, boolean hasBody) {
        int status = response.getStatus();
        String responseBody;

        if (!hasBody) {
            responseBody = "";
        } else {
            responseBody = hidePassword(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
        }

        if (status < 400) {
            log.info("Response: [transactionId={}, status={}, body={}]",
                    transactionId,
                    status,
                    responseBody.isEmpty() ? "No Body" : responseBody);
        } else {
            log.error("Error Response: [transactionId={}, status={}, body={}]",
                    transactionId,
                    status,
                    responseBody.isEmpty() ? "No Body" : responseBody);
        }
    }

    private boolean checkUrl(String url) {
        return url.startsWith("/api/v1/trainee") || url.startsWith("/api/v1/trainer") || url.startsWith("/api/v1/training");
    }

    static class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

        private byte[] rawData;
        private final HttpServletRequest request;
        private final ResettableServletInputStream servletStream;

        public ResettableStreamHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
            this.servletStream = new ResettableServletInputStream();
        }

        public void resetInputStream() {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader(), StandardCharsets.UTF_8);
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return servletStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader(), StandardCharsets.UTF_8);
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return new BufferedReader(new InputStreamReader(servletStream));
        }

        private static class ResettableServletInputStream extends ServletInputStream {

            private InputStream stream;

            @Override
            public int read() throws IOException {
                return stream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        }
    }
}

