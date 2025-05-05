package com.example.project.Controller;

import com.example.project.Request.ProjectMemberRequest;
import com.example.project.Request.UpdateProjectMemberRequest;
import com.example.project.Service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/project/member")
@RequiredArgsConstructor
public class ProjectMemberController {
  private final ProjectMemberService projectMemberService;

  @PostMapping("/add")
  ResponseEntity<Map<String, Object>> addProjectMember(@RequestBody ProjectMemberRequest projectMemberRequest) {
    return projectMemberService.addProjectMember(projectMemberRequest);
  }

  @PutMapping("/update")
  ResponseEntity<Map<String, Object>> updateProjectMember(@RequestBody UpdateProjectMemberRequest updateProjectMemberRequest) {
    return projectMemberService.updateProjectMember(updateProjectMemberRequest);
  }

  @DeleteMapping("/delete")
  ResponseEntity<Map<String, Object>> deleteProjectMember(@RequestParam Integer id) {
    return projectMemberService.deleteProjectMember(id);
  }
}
