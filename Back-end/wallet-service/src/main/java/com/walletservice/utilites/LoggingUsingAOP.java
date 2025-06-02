package com.walletservice.utilites;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingUsingAOP {

    @Pointcut("execution(* com.walletservice.controller.*.*(..))")
    public void controllerLayer() {}

    @Before("controllerLayer()")
    public void logBefore(JoinPoint joinPoint) {
        log.info(
            "entering method {}  with :{}",
            joinPoint.getSignature().getName(),
            Arrays.toString(joinPoint.getArgs())
        );
    }

    @AfterReturning(pointcut = "controllerLayer()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        log.info(
            "finishing method {}  with :{}",
            joinPoint.getSignature().getName(),
            result
        );
    }

    @Around("controllerLayer()")
    public void executionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        joinPoint.proceed();

        long time = System.currentTimeMillis() - start;
        log.info(
            " method {}  taken in ms :{}",
            joinPoint.getSignature().getName(),
            time
        );
    }
}
