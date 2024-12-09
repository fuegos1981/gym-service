package com.gym.app.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionIdFilterTest {

    private static final String TRANSACTION_ID = "X-Transaction-Id";

    @Mock
    private ServerWebExchange exchange;
    @InjectMocks
    private TransactionIdFilter transactionIdFilter;

    @Test
    void checkIfInAddTransactionIdHeader_shouldMutateRequest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ServerHttpRequest originalRequest = mock(ServerHttpRequest.class);
        ServerHttpRequest.Builder builder = mock(ServerHttpRequest.Builder.class);

        when(originalRequest.mutate()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(originalRequest);

        when(exchange.getRequest()).thenReturn(originalRequest);
        when(exchange.mutate()).thenCallRealMethod();

        String transactionId = UUID.randomUUID().toString();

        Method method = TransactionIdFilter.class.getDeclaredMethod("addTransactionIdHeader", ServerWebExchange.class, String.class);
        method.setAccessible(true);

        ServerWebExchange updatedExchange = (ServerWebExchange) method.invoke(transactionIdFilter, exchange, transactionId);

        verify(builder).header(TRANSACTION_ID, transactionId);
        verify(builder).build();
    }
}