package com.example.project.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
  private Integer id;

  private String name;

  private String avatar;
}
