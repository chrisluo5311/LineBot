package com.infotran.springboot.config;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogInfoAspect {

    @Around("@annotation(com.infotran.springboot.annotation.LogInfo)")
    public Object around(ProceedingJoinPoint joinpoint) throws Throwable {
        String className = joinpoint.getSignature().getDeclaringType().getSimpleName();
        String annotatedMethodName = joinpoint.getSignature().getName();
        Object[] args = joinpoint.getArgs();
        for (Object signatureArg : args){
            System.out.print("Args: " + signatureArg);
        }
        log.info("[{}.{}] start", className, annotatedMethodName);
        Object object = joinpoint.proceed();
        log.info("[{}.{}] end", className, annotatedMethodName);

        return object;
    }

}

