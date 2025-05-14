package com.example.project.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectResponse {
  private Integer id;
  private String name;
  private int createBy;
  private int memberCount;
}
