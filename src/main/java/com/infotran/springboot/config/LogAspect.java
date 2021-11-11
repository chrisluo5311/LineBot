package com.infotran.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author chris
 */
@Aspect
@Slf4j
@Component
public class LogAspect {

    @Pointcut("@annotation(com.infotran.springboot.annotation.LogExecutionTime)")
    private void logAspectPointcut(){
    }

    @Around("logAspectPointcut()")
    public Object logInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("======================方法開始==========================");
        Object object = joinPoint.proceed();
        long executionTime = (System.currentTimeMillis()-start)/1000;
        log.info("=================方法結束 方法耗時:{} ms==================",executionTime);
        return object;
    }
}
