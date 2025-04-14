package com.example.project.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProjectRequest {
    private Integer id;

    @NotBlank(message = "Name không được để trống !")
    private String name;

    private String description;

    private String status;
}
