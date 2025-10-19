package com.api.projects.configs;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

  @Value("${rate-limit.requests:10}")
  private int requests;

  @Value("${rate-limit.duration:60}")
  private int durationInSeconds;

  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

  public Bucket resolveBucket(String key) {
    return cache.computeIfAbsent(key, k -> createNewBucket());
  }

  private Bucket createNewBucket() {
    Bandwidth limit =
        Bandwidth.classic(
            requests, Refill.intervally(requests, Duration.ofSeconds(durationInSeconds)));
    return Bucket.builder().addLimit(limit).build();
  }
}
