package com.api.projects.configs;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

  private final RateLimitConfig rateLimitConfig;

  public RateLimitInterceptor(RateLimitConfig rateLimitConfig) {
    this.rateLimitConfig = rateLimitConfig;
  }

  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler)
      throws Exception {

    String key = getClientIP(request);
    Bucket bucket = rateLimitConfig.resolveBucket(key);
    ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

    if (probe.isConsumed()) {
      response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
      return true;
    } else {
      long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
      response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
      response.sendError(
          HttpStatus.TOO_MANY_REQUESTS.value(), "Muitas requisições - tente novamente mais tarde");
      return false;
    }
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}
