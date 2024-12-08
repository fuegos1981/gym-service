package com.gym.crm.aspect;

import com.gym.crm.constants.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class OperationLoggingAspect {

    @Around("execution(* com.gym.crm.facade.ServiceFacade.*(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String transactionId = MDC.get(GlobalConstants.TRANSACTION_ID);

        Object[] args = joinPoint.getArgs();
        String operation = joinPoint.getSignature().toShortString();
        if (operation.contains("changePassword")) {
            args[1] = "**********";
        }

        log.info("Operation Start: [transactionId={}, operation={}, , args={}]",
                transactionId, operation, args);

        Object result = joinPoint.proceed();

        log.info("Operation End: [transactionId={}, operation={}, result={}]",
                transactionId, operation, result);

        return result;
    }
}
