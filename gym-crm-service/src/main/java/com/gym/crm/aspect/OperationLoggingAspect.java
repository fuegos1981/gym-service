package com.gym.crm.aspect;

import com.gym.crm.constants.GlobalConstants;
import com.gym.crm.dto.UserDetailsResponse;
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
        Object detail = operation.contains("changePassword") ? getResult(args[1].toString()) : args;

        log.info("Operation Start: [transactionId={}, operation={}, , args={}]",
                transactionId, operation, detail);

        Object result = joinPoint.proceed();

        log.info("Operation End: [transactionId={}, operation={}, result={}]",
                transactionId, operation, hidePassword(result, operation));

        return result;
    }

    private Object getResult(String username) {
        return "{username=" + username + ", password=**********}";
    }

    private Object hidePassword(Object result, String operation) {
        if (operation.contains("createTraine")) {
            return getResult(((UserDetailsResponse) result).getUsername());
        }
        return result;
    }
}
