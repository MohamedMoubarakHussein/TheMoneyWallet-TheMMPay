package com.themoneywallet.authenticationservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RequestSignUpAspect {

    @Pointcut(
        "execution(* com.themoneywallet.authenticationservice.controller..*(..))"
    )
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {
        log.info(" Called: " + joinPoint.getSignature());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                log.info(" Argument: " + arg.toString());
            }
        }
    }
}
