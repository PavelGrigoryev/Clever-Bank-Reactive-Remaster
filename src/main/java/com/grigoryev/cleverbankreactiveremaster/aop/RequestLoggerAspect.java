package com.grigoryev.cleverbankreactiveremaster.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class RequestLoggerAspect {

    @Before("within(@com.grigoryev.cleverbankreactiveremaster.aop.RequestLoggable *)")
    public void logRequest(JoinPoint joinPoint) {
        log.info("Request: {}", Arrays.toString(joinPoint.getArgs()));
    }

}
