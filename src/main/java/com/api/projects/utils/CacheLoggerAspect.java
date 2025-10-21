package com.api.projects.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CacheLoggerAspect {

  @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
  public Object logCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    log.debug("Checking cache for method: {}", methodName);

    Object result = joinPoint.proceed();

    log.debug("Method {} executed, result cached", methodName);
    return result;
  }

  @Around("@annotation(org.springframework.cache.annotation.CacheEvict)")
  public Object logCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    log.debug("Evicting cache for method: {}", methodName);

    return joinPoint.proceed();
  }
}
