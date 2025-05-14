package com.example.project.Service;

import com.example.project.Entity.Project;
import com.example.project.Entity.ProjectMember;
import com.example.project.Entity.Task;
import com.example.project.Entity.User;
import com.example.project.Enum.ProjectRole;
import com.example.project.Mapper.ProjectMapper;
import com.example.project.Mapper.UpdateProjectMapper;
import com.example.project.Repository.ProjectMemberRepository;
import com.example.project.Repository.ProjectRepository;
import com.example.project.Repository.TaskRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Request.ProjectRequest;
import com.example.project.Request.UpdateProjectRequest;
import com.example.project.Response.ProjectResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
  private final ProjectRepository projectRepository;
  private final ProjectMemberRepository projectMemberRepository;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final ProjectMapper projectMapper;
  private final UpdateProjectMapper updateProjectMapper;

  @Transactional
  public ResponseEntity<Map<String, Object>> createProject(ProjectRequest projectRequest) {
    Map<String, Object> response = new HashMap<>();

    try {
      User user = userRepository.findById(projectRequest.getCreateById())
        .orElseThrow(() -> new RuntimeException("User not found"));
      if (projectRepository.existsByNameAndCreateBy(projectRequest.getName(), user)) {
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", "Project đã trùng tên !");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
      }
      Project project = projectMapper.projectRequestToProject(projectRequest);
      project.setCreateBy(user);
      projectRepository.save(project);

      addMember(project, user);

      response.put("status", HttpStatus.CREATED.value());
      response.put("message", "Tạo project thành công");
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  private void addMember(Project project, User user) {
    ProjectRole role = ProjectRole.valueOf("ADMIN");
    ProjectMember projectMember = ProjectMember.builder()
      .project(project)
      .user(user)
      .role(role)
      .build();
    projectMemberRepository.save(projectMember);
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> updateProject(UpdateProjectRequest updateProjectRequest) {
    Map<String, Object> response = new HashMap<>();

    try {
      Project project = projectRepository.findById(updateProjectRequest.getId())
        .orElseThrow(() ->
          new RuntimeException("Project not found with id: " + updateProjectRequest.getId()));
      updateProjectMapper.updateProjectFromRequest(updateProjectRequest, project);
      projectRepository.save(project);
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Cập nhật project thành công");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> deleteProject(Integer id) {
    Map<String, Object> response = new HashMap<>();

    try {
      if (!projectRepository.existsById(id)) {
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", "Project không tồn tại");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      taskRepository.deleteAllByProjectId(id);
      projectMemberRepository.deleteAllByProjectId(id);
      projectRepository.deleteById(id);

      response.put("status", HttpStatus.OK.value());
      response.put("message", "Xóa project thành công");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      e.printStackTrace();
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> allProjects(Integer id) {
    Map<String, Object> response = new HashMap<>();

    try {
      User user = userRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("User not found"));

      // Lấy tất cả project mà user đang tham gia (dù là creator hay member)
      List<ProjectMember> projectMembers = projectMemberRepository.findByUser(user);

      List<ProjectResponse> projectResponses = projectMembers.stream()
              .map(pm -> {
                Project project = pm.getProject();
                return new ProjectResponse(
                        project.getId(),
                        project.getName(),
                        project.getProjectMembers() != null ? project.getProjectMembers().size() : 0
                );
              })
              .collect(Collectors.toList());

      response.put("status", HttpStatus.OK.value());
      response.put("data", projectResponses);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }


  @Transactional
  public ResponseEntity<Map<String, Object>> allTasksInProject(Integer id) {
    Map<String, Object> response = new HashMap<>();

    try{
      Project project = projectRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Project not found: " + id));
      List<Task> tasks = taskRepository.findByProject(project);
      response.put("status", HttpStatus.OK.value());
      response.put("tasks", tasks);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}
