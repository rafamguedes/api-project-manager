package com.api.projects.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig implements OpenApiCustomizer {

  @Override
  public void customise(OpenAPI openApi) {
    Info info =
        new Info()
            .title("Projects Management API")
            .description("API for managing projects and tasks")
            .version("1.0.0");

    openApi.info(info);
  }
}
