package com.api.projects;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class RateLimitTest {

  private static final Logger log = LoggerFactory.getLogger(RateLimitTest.class);

  private static final String PROJECT_PATH = "/api/v1/projects";

  @Autowired private MockMvc mockMvc;

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void shouldAllowRequestsWithinLimit() throws Exception {
    log.info("Testing rate limit within limit");

    MvcResult result =
        mockMvc
            .perform(get(PROJECT_PATH).header("X-Forwarded-For", "192.168.1.100"))
            .andExpect(status().isOk())
            .andExpect(header().exists("X-Rate-Limit-Remaining"))
            .andReturn();

    String remaining = result.getResponse().getHeader("X-Rate-Limit-Remaining");
    log.info("Tokens remaining: {}", remaining);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void shouldBlockRequestsAfterLimit() throws Exception {
    log.info("Testing rate limit blocking after limit");

    for (int i = 0; i < 40; i++) {
      MvcResult result =
          mockMvc
              .perform(get(PROJECT_PATH).header("X-Forwarded-For", "192.168.1.200"))
              .andExpect(status().isOk())
              .andReturn();

      String remaining = result.getResponse().getHeader("X-Rate-Limit-Remaining");
      log.info("Request {}/40 - Remaining: {}", i + 1, remaining);
    }

    log.warn("Next request should be blocked");

    MvcResult blockedResult =
        mockMvc
            .perform(get(PROJECT_PATH).header("X-Forwarded-For", "192.168.1.200"))
            .andExpect(status().isTooManyRequests())
            .andExpect(header().exists("X-Rate-Limit-Retry-After-Seconds"))
            .andReturn();

    String retryAfter = blockedResult.getResponse().getHeader("X-Rate-Limit-Retry-After-Seconds");
    log.warn("Request blocked! Retry after: {}s", retryAfter);
  }
}
