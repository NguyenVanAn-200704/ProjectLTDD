package com.example.project.Response;

import com.example.project.Entity.User;
import com.example.project.Enum.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TaskResponse {
  private Integer id;
  private String title;
  private String description;
  private String status;
  private TaskPriority priority;
  private User user;
  private LocalDate dueDate;
  private LocalDate createdDate;
}
