package com.gym.crm.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OperationLoggingAspectTest {

    private OperationLoggingAspect aspect;
    @Mock
    private ProceedingJoinPoint joinPointMock;
    @Mock
    private Signature signatureMock;

    @BeforeEach
    public void setup() {
        aspect = new OperationLoggingAspect();
        MDC.put("transactionId", "12345");
    }

    @Test
    public void checkIfLogOperationIsNotChangePassword() throws Throwable {
        Object[] args = {"user1", "something"};

        when(signatureMock.toShortString()).thenReturn("public void com.gym.crm.facade.ServiceFacade.someOperation(String, String)");
        when(joinPointMock.getArgs()).thenReturn(args);
        when(joinPointMock.getSignature()).thenReturn(signatureMock);
        when(joinPointMock.proceed()).thenReturn("success");

        Object result = aspect.logOperation(joinPointMock);

        assertEquals("something", args[1]);

        assertEquals("success", result);
    }

    @Test
    public void checkIfLogOperationIsChangePassword() throws Throwable {
        Object[] args = {"user1", "password123"};
        when(signatureMock.toShortString()).thenReturn("public void com.gym.crm.facade.ServiceFacade.changePassword(String, String)");

        when(joinPointMock.getArgs()).thenReturn(args);
        when(joinPointMock.getSignature()).thenReturn(signatureMock);
        when(joinPointMock.proceed()).thenReturn("result");

        Object result = aspect.logOperation(joinPointMock);

        assertEquals("**********", args[1]);
        assertEquals("result", result);
    }

    @Test
    public void checkIfLogOperationWithNoArgs() throws Throwable {
        when(joinPointMock.getArgs()).thenReturn(new Object[]{});
        when(signatureMock.toShortString()).thenReturn("public void com.gym.crm.facade.ServiceFacade.noArgsOperation()");
        when(joinPointMock.getSignature()).thenReturn(signatureMock);
        when(joinPointMock.proceed()).thenReturn("result");

        Object result = aspect.logOperation(joinPointMock);

        assertEquals("result", result);
    }

}