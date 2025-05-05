package com.example.project.Request;

import lombok.Getter;

@Getter
public class ProjectMemberRequest {
  private Integer projectId;

  private String email;

  private String role;
}
