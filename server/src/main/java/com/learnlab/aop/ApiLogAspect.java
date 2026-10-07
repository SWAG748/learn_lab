package com.learnlab.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiLogAspect {

  private static final Logger log = LoggerFactory.getLogger(ApiLogAspect.class);

  @Around("@within(org.springframework.web.bind.annotation.RestController)")
  public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
    long started = System.nanoTime();
    String signature = joinPoint.getSignature().toShortString();
    try {
      Object result = joinPoint.proceed();
      log.info("API {} 完成，用时 {} ms", signature, elapsed(started));
      return result;
    } catch (Throwable error) {
      log.warn("API {} 失败，用时 {} ms：{}", signature, elapsed(started), error.getMessage());
      throw error;
    }
  }

  private static long elapsed(long started) {
    return (System.nanoTime() - started) / 1_000_000;
  }
}
