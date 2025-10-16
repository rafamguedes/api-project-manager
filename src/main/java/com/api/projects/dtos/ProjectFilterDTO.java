package com.api.projects.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterDTO {
  private Integer page = 0;
  private Integer size = 10;
  private String sortBy = "id";
  private String direction = "ASC";
}
