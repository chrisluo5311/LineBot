package com.infotran.springboot.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@org.aspectj.lang.annotation.Aspect
public class LogAspect {

    @Pointcut("execution(* com.infotran.springboot.*.*.*(..))")
    public void LogAspect() {};

    @Before("LogAspect()")
    public void dobefore(JoinPoint joinpoint) {
        System.out.println("====之前====");
    }

    @After("LogAspect()")
    public void doAfter(JoinPoint joinpoint) {
        System.out.println("====之後====");
    }

}
