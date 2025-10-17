package com.api.projects.dtos.task;

import com.api.projects.enums.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaskStatusUpdateDTO {

  @ApiModelProperty(
      value = "Status of the task",
      required = true,
      allowableValues = "TODO,DOING,DONE")
  private Status status;
}
