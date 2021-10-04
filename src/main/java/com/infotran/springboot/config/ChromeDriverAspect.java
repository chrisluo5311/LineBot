package com.infotran.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Aspect
@Slf4j
@Component
public class ChromeDriverAspect {

    @Pointcut("@annotation(com.infotran.springboot.annotation.ChromeWebDriver) && args(org.openqa.selenium.WebDriver,..)")
    public void ChromeDriverAspect(){
    }

    @Around("ChromeDriverAspect()")
    public Object toChromeDriver(ProceedingJoinPoint joinPoint) throws Throwable {
        WebDriver arg = (ChromeDriver)joinPoint.getArgs()[0];
        Object result = joinPoint.proceed(new Object[]{arg});
        return result;
    }

}
