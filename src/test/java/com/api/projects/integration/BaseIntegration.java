package com.api.projects.integration;

import com.api.projects.TestContainersConfiguration;
import com.api.projects.services.AuthService;
import com.api.projects.services.ProjectService;
import com.api.projects.services.TaskService;
import com.api.projects.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
public abstract class BaseIntegration {

  @Autowired protected MockMvc mockMvc;

  @Autowired protected ObjectMapper objectMapper;

  @MockitoBean protected AuthService authService;

  @MockitoBean protected ProjectService projectService;

  @MockitoBean protected TaskService taskService;

  @MockitoBean protected UserService userService;
}
