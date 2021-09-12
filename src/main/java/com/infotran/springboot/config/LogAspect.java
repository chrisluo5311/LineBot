package com.infotran.springboot.config;

import com.infotran.springboot.annotation.LogInfo;
import io.lettuce.core.output.ScanOutput;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@org.aspectj.lang.annotation.Aspect
@Slf4j
public class LogAspect {

    @Pointcut(value = "@annotation(com.infotran.springboot.annotation.LogInfo)")
    private void LogAspect() {};

    @Before(value = "LogAspect() && @annotation(logInfo)")
    public void dobefore(JoinPoint joinpoint, LogInfo logInfo) {
        //class name
        String clsName = joinpoint.getSignature().getDeclaringType().getSimpleName();
        //method name
        String mthName = joinpoint.getSignature().getName();
        List<String> args = Arrays.asList(String.valueOf(joinpoint.getArgs()));
        args.stream().collect(Collectors.joining(",","[","]"));
        log.info("類名:{} 方法名:{} 參數:{} 需注意的事項:{}",clsName,mthName,args,logInfo.warning());
    }



}
