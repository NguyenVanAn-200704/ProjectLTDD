package com.example.project.Request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProjectRequest {
  private Integer createById;

  @NotBlank(message = "Name không được để trống !")
  private String name;

  private String description;
}
