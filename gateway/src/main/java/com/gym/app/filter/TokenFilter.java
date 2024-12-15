package com.gym.app.filter;

import com.gym.app.constants.GlobalConstants;
import com.gym.app.model.User;
import com.gym.app.service.TokenBlacklistService;
import com.gym.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TokenFilter implements WebFilter {

    private final String gatewaySecret;
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistService blacklistService;

    public TokenFilter(@Value("${gateway.secret}") String gatewaySecret, UserService userService, JwtProvider jwtProvider, TokenBlacklistService blacklistService) {
        this.gatewaySecret = gatewaySecret;
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.blacklistService = blacklistService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange = setHeader(exchange, "Gateway", gatewaySecret);
        String requestPath = exchange.getRequest().getURI().getPath();

        if (isPermitAllUrl(requestPath)) {
            return chain.filter(exchange);
        }

        String jwt = extractJwt(exchange.getRequest());
        if (jwt == null) {
            return chain.filter(exchange);
        }

        ServerWebExchange finalExchange = exchange;

        return validateAndAuthenticate(jwt)
                .flatMap(authenticated -> {
                    controlBlackList(jwt, requestPath);
                    ServerWebExchange mutatedExchange = setHeader(finalExchange, "X-USER", jwtProvider.extractUsername(jwt));
                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private ServerWebExchange setHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate()
                .request(builder -> builder.header(name, value))
                .build();
    }


    private void controlBlackList(String token, String path) {
        if (GlobalConstants.LOGOUT_PATH.stream().anyMatch(path::startsWith)) {
            blacklistService.blacklistToken(token);
        }
    }

    private boolean isPermitAllUrl(String path) {
        return GlobalConstants.PERMIT_ALL_URLS.stream().anyMatch(path::startsWith);
    }

    private String extractJwt(ServerHttpRequest request) {
        String authorizationHeader = request.getHeaders().getFirst("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    }

    private Mono<Boolean> validateAndAuthenticate(String jwt) {
        if (blacklistService.isTokenBlacklisted(jwt)) {
            log.warn("Token is blacklisted");
            return Mono.empty();
        }

        return Mono.justOrEmpty(jwtProvider.extractUsername(jwt))
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> authenticateUser(jwt, user))
                .onErrorResume(e -> {
                    log.warn("Authentication failed: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Boolean> authenticateUser(String jwt, User user) {
        if (jwtProvider.validateToken(jwt, user)) {
            return Mono.just(true);
        }

        return Mono.error(new RuntimeException("Invalid JWT Token"));
    }
}
