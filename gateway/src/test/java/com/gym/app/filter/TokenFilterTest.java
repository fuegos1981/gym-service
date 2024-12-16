package com.gym.app.filter;

import com.gym.app.model.User;
import com.gym.app.service.TokenBlacklistService;
import com.gym.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenFilterTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TokenBlacklistService blacklistService;

    @InjectMocks
    private TokenFilter tokenFilter;

    private WebFilterChain webFilterChain;

    private static final String VALID_JWT = "valid.jwt.token";
    private static final String USERNAME = "testUser";
    private static final String GATEWAY_SECRET = "testGatewaySecret";
    private static final String AUTH_HEADER = "Bearer " + VALID_JWT;
    private static final String PERMITTED_PATH = "/public/api";
    private static final String SECURED_PATH = "/secured/api";

    @BeforeEach
    void setUp() {
        tokenFilter = new TokenFilter(GATEWAY_SECRET, userService, jwtProvider, blacklistService);

        webFilterChain = Mockito.mock(WebFilterChain.class);
        when(webFilterChain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void checkIfPassThroughPermitAllUrlsIsWorked() {
        ServerWebExchange exchange = createExchange(PERMITTED_PATH, null);

        Mono<Void> result = tokenFilter.filter(exchange, webFilterChain);

        StepVerifier.create(result).verifyComplete();

        verifyNoInteractions(jwtProvider, userService, blacklistService);
    }

    @Test
    void checkIfAuthenticateValidJwtIsWorked() {
        User user = new User();
        user.setUsername(USERNAME);

        ServerWebExchange exchange = createExchange(SECURED_PATH, AUTH_HEADER);

        when(jwtProvider.extractUsername(VALID_JWT)).thenReturn(USERNAME);
        when(userService.findUserByUsername(USERNAME)).thenReturn(Mono.just(user));
        when(jwtProvider.validateToken(VALID_JWT, user)).thenReturn(true);

        Mono<Void> result = tokenFilter.filter(exchange, webFilterChain);

        StepVerifier.create(result).verifyComplete();

        verify(userService).findUserByUsername(USERNAME);
        verify(jwtProvider).validateToken(VALID_JWT, user);
    }

    @Test
    void checkIfRejectBlacklistedTokenIsWorked() {
        ServerWebExchange exchange = createExchange(SECURED_PATH, AUTH_HEADER);

        when(blacklistService.isTokenBlacklisted(VALID_JWT)).thenReturn(true);

        Mono<Void> result = tokenFilter.filter(exchange, webFilterChain);

        StepVerifier.create(result).verifyComplete();

        verify(blacklistService).isTokenBlacklisted(VALID_JWT);
        verifyNoInteractions(jwtProvider, userService);
    }

    @Test
    void checkIfPassThroughWhenNoTokenProvided() {
        ServerWebExchange exchange = createExchange(SECURED_PATH, null);

        Mono<Void> result = tokenFilter.filter(exchange, webFilterChain);

        StepVerifier.create(result).verifyComplete();

        verifyNoInteractions(jwtProvider, userService, blacklistService);
    }

    private ServerWebExchange createExchange(String path, String authHeader) {
        MockServerHttpRequest.BaseBuilder<?> requestBuilder = MockServerHttpRequest.get(path);

        if (authHeader != null) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, authHeader);
        }

        return MockServerWebExchange.from(requestBuilder.build());
    }

}