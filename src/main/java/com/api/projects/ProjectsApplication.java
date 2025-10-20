package com.api.projects;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProjectsApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProjectsApplication.class, args);
  }
}
