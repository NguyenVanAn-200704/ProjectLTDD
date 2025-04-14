package com.example.project.Controller;

import com.example.project.Request.ProjectRequest;
import com.example.project.Request.UserRequest;
import com.example.project.Service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/create")
    ResponseEntity<Map<String, Object>> createProject(@Valid @RequestBody ProjectRequest projectRequest) {
        return projectService.createProject(projectRequest);
    }
}
