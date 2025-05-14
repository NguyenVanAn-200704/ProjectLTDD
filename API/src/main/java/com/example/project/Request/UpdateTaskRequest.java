package com.example.project.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateTaskRequest {
  private Integer id;

  @NotBlank(message = "Title không được để trống !")
  private String title;

  @NotBlank(message = "Description không được để trống !")
  private String description;

  private String status;

  private String fileUrl;

  private String priority;

  private String email;

  @NotNull(message = "DueDate không được để trống !")
  private LocalDate dueDate;
}
