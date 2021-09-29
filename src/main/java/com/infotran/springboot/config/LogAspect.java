package com.infotran.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LogAspect {

    @Around("@annotation(com.infotran.springboot.annotation.LogExecutionTime)")
    public Object logInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("======================回覆開始==========================");
        Object object = joinPoint.proceed();
        long executionTime = System.currentTimeMillis()-start;
        log.info("=================回覆結束 方法耗時:{} ms==================",executionTime);
        return object;
    }
}
