package com.example.project.Controller;

import com.example.project.Request.ProjectMemberRequest;
import com.example.project.Request.UpdateProjectMemberRequest;
import com.example.project.Service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/project") // Thay đổi RequestMapping để phù hợp với API bạn muốn
@RequiredArgsConstructor
public class ProjectMemberController {
  private final ProjectMemberService projectMemberService;

  @PostMapping("/member/add")
  ResponseEntity<Map<String, Object>> addProjectMember(@RequestBody ProjectMemberRequest projectMemberRequest) {
    return projectMemberService.addProjectMember(projectMemberRequest);
  }

  @PutMapping("/member/update")
  ResponseEntity<Map<String, Object>> updateProjectMember(@RequestBody UpdateProjectMemberRequest updateProjectMemberRequest) {
    return projectMemberService.updateProjectMember(updateProjectMemberRequest);
  }

  @DeleteMapping("/member/delete")
  ResponseEntity<Map<String, Object>> deleteProjectMember(@RequestParam Integer id) {
    return projectMemberService.deleteProjectMember(id);
  }

  @GetMapping("/{projectId}/members")
  ResponseEntity<Map<String, Object>> getAllMembers(@PathVariable Integer projectId) {
    return projectMemberService.getAllMembers(projectId);
  }
}