package com.example.project.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TaskRequest {
  @NotBlank(message = "Title không được để trống !")
  private String title;

  @NotBlank(message = "Description không được để trống !")
  private String description;

  private String priority;

  private Integer projectId;

  private Integer userId;

  @NotNull(message = "DueDate không được để trống !")
  private LocalDate dueDate;
}
