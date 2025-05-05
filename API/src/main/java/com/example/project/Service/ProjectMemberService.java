package com.example.project.Service;

import com.example.project.Entity.Project;
import com.example.project.Entity.ProjectMember;
import com.example.project.Entity.User;
import com.example.project.Enum.ProjectRole;
import com.example.project.Repository.ProjectMemberRepository;
import com.example.project.Repository.ProjectRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Request.ProjectMemberRequest;
import com.example.project.Request.UpdateProjectMemberRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {
  private final ProjectMemberRepository projectMemberRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;

  @Transactional
  public ResponseEntity<Map<String, Object>> addProjectMember(ProjectMemberRequest projectMemberRequest) {
    Map<String, Object> response = new HashMap<>();
    try {
      Project project = projectRepository.findById(projectMemberRequest.getProjectId()).orElseThrow(
        () -> new RuntimeException("Project not found"));
      User user = userRepository.findByEmail(projectMemberRequest.getEmail()).orElseThrow(
        () -> new RuntimeException("User not found"));
      Optional<ProjectMember> existing = projectMemberRepository.findByProjectAndUser(project, user);
      if (existing.isPresent()) {
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Người dùng đã là thành viên của project này");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }
      ProjectRole role = ProjectRole.valueOf(projectMemberRequest.getRole().toUpperCase());
      ProjectMember projectMember = ProjectMember.builder()
        .project(project)
        .user(user)
        .role(role)
        .build();
      projectMemberRepository.save(projectMember);
      response.put("status", HttpStatus.CREATED.value());
      response.put("message", "Thêm thành viên thành công");
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> updateProjectMember(UpdateProjectMemberRequest updateProjectMemberRequest) {
    Map<String, Object> response = new HashMap<>();
    try {
      ProjectMember projectMember = projectMemberRepository.findById(updateProjectMemberRequest.getId()).orElseThrow(
        () -> new RuntimeException("Project not found: " + updateProjectMemberRequest.getId())
      );
      projectMember.setRole(ProjectRole.valueOf(updateProjectMemberRequest.getRole().toUpperCase()));
      projectMemberRepository.save(projectMember);
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Cập nhật project-member thành công");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> deleteProjectMember(Integer id) {
    Map<String, Object> response = new HashMap<>();
    try {
      projectMemberRepository.deleteById(id);
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Xóa project-member thành công");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}
