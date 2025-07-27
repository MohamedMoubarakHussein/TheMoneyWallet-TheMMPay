package com.themoneywallet.usermanagmentservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {


    @Pointcut("execution(* com.themoneywallet.authenticationservice..*(..)) && !within(jakarta.servlet.Filter+) && !within(org.springframework.web.filter..*) &&!within(com.themoneywallet.authenticationservice.config.Security..*)")
    public void myPath(){

    }
    @Before("myPath()")
    public void before(JoinPoint joinPoint) {
        log.info("BEFORE: {} with args {}", 
            joinPoint.getSignature(), 
            joinPoint.getArgs()
        );
    }

    @AfterReturning(pointcut = "myPath()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        log.info("SUCCESS: {} returned {}", 
            joinPoint.getSignature(), 
            result
        );
    }

    @AfterThrowing(pointcut = "myPath()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("EXCEPTION in {}: {}", 
            joinPoint.getSignature(), 
            ex.toString()
        );
    }

    @Around("myPath()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();

        log.info("AROUND START: {}", joinPoint.getSignature());

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            log.error("AROUND EXCEPTION in {}: {}", joinPoint.getSignature(), ex.toString());
            throw ex;
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        log.info("PERFORMANCE: {} executed in {} ms", 
            joinPoint.getSignature(), 
            durationMs
        );

        return result;
    }
}