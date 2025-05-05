package com.example.project.Controller;

import com.example.project.Request.ProjectRequest;
import com.example.project.Request.UpdateProjectRequest;
import com.example.project.Service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @PutMapping("/update")
  ResponseEntity<Map<String, Object>> updateProject(@Valid @RequestBody UpdateProjectRequest updateProjectRequest) {
    return projectService.updateProject(updateProjectRequest);
  }

  @DeleteMapping("/delete")
  ResponseEntity<Map<String, Object>> deleteProject(@RequestParam Integer id) {
    return projectService.deleteProject(id);
  }

  @GetMapping("/all")
  ResponseEntity<Map<String, Object>> allProject(@RequestParam Integer id) {
    return projectService.allProjects(id);
  }
}
