package com.api.projects.dtos.task;

import com.api.projects.enums.Priority;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaskPriorityUpdateDTO {

  @ApiModelProperty(
      value = "Priority of the task",
      required = true,
      allowableValues = "LOW,MEDIUM,HIGH")
  private Priority priority;
}
