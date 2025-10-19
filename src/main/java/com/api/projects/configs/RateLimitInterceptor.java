package com.api.projects.configs;

import com.api.projects.exceptions.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

  private static final Logger log = LoggerFactory.getLogger(RateLimitInterceptor.class);
  private final RateLimitConfig rateLimitConfig;

  public RateLimitInterceptor(RateLimitConfig rateLimitConfig) {
    this.rateLimitConfig = rateLimitConfig;
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String requestId = UUID.randomUUID().toString();
    String clientIp = getClientIP(request);
    String endpoint = request.getRequestURI();

    MDC.put("requestId", requestId);
    MDC.put("ip", clientIp);

    Bucket bucket = rateLimitConfig.resolveBucket(clientIp);
    ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

    if (probe.isConsumed()) {
      response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
      return true;
    } else {
      long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

      log.warn(
          "Rate limit exceeded - IP: {}, Endpoint: {}, RetryAfter: {}s",
          clientIp,
          endpoint,
          waitForRefill);

      throw new RateLimitExceededException(
          "Rate limit exceeded. Try again in " + waitForRefill + " seconds.", waitForRefill);
    }
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    MDC.clear();
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}
