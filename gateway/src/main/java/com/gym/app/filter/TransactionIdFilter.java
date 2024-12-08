package com.gym.app.filter;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class TransactionIdFilter implements WebFilter {

    private static final String TRANSACTION_ID = "X-Transaction-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String transactionId = exchange.getRequest().getHeaders().getFirst(TRANSACTION_ID);
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
            MDC.put(TRANSACTION_ID, transactionId);
            exchange = addTransactionIdHeader(exchange, transactionId);
        }

        return chain.filter(exchange);
    }

    private ServerWebExchange addTransactionIdHeader(ServerWebExchange exchange, String transactionId) {
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header(TRANSACTION_ID, transactionId)
                .build();
        return exchange.mutate().request(mutatedRequest).build();
    }
}




