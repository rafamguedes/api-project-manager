package com.api.projects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void shouldAllowRequestsWithinLimit() throws Exception {
    mockMvc
        .perform(get("/api/v1/projects").header("X-Forwarded-For", "192.168.1.100"))
        .andExpect(status().isOk())
        .andExpect(header().exists("X-Rate-Limit-Remaining"));
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void shouldBlockRequestsAfterLimit() throws Exception {
    // Make 10 requests to reach the limit
    for (int i = 0; i < 10; i++) {
      mockMvc.perform(get("/api/v1/projects")).andExpect(status().isOk());
    }

    // 11th request should be blocked
    mockMvc
        .perform(get("/api/v1/projects"))
        .andExpect(status().isTooManyRequests())
        .andExpect(header().exists("X-Rate-Limit-Retry-After-Seconds"));
  }
}
