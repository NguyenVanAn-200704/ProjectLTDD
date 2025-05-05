package com.example.project.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProjectMemberRequest {
  private Integer id;

  private String role;
}
