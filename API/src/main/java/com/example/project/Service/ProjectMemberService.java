package com.example.project.Service;

import com.example.project.Entity.Project;
import com.example.project.Entity.ProjectMember;
import com.example.project.Entity.Task;
import com.example.project.Entity.User;
import com.example.project.Enum.ProjectRole;
import com.example.project.Repository.ProjectMemberRepository;
import com.example.project.Repository.ProjectRepository;
import com.example.project.Repository.TaskRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Request.ProjectMemberRequest;
import com.example.project.Request.UpdateProjectMemberRequest;
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
public class ProjectMemberService {
  private final ProjectMemberRepository projectMemberRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final TaskRepository taskRepository;
  private final EmailService emailService;

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

      // Gửi email thông báo cho người dùng được thêm
      String subject = "Bạn được thêm vào một Dự án mới";
      String body = String.format(
              "Chào %s,\n\nBạn vừa được thêm vào dự án %s với vai trò %s.\n\n" +
                      "Vui lòng kiểm tra và bắt đầu tham gia dự án.\n" +
                      "Trân trọng,\nHệ thống quản lý dự án",
              user.getName() != null ? user.getName() : user.getEmail(),
              project.getName(),
              role.toString()
      );
      emailService.sendEmail(user.getEmail(), subject, body);

      response.put("status", HttpStatus.CREATED.value());
      response.put("message", "Thêm thành viên thành công");
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // Phương thức addTask giữ nguyên, không cần sửa

  @Transactional
  public ResponseEntity<Map<String, Object>> updateMemberRole(UpdateProjectMemberRequest updateProjectMemberRequest) {
    Map<String, Object> response = new HashMap<>();
    try {
      ProjectMember member = projectMemberRepository.findById(updateProjectMemberRequest.getId())
              .orElseThrow(() -> new RuntimeException("Member not found"));
      Project project = member.getProject();
      if (member.getRole() == ProjectRole.ADMIN && project.getCreateBy().getId() != updateProjectMemberRequest.getCurrentUserId()) {
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("message", "Chỉ người tạo dự án có thể thay đổi vai trò của Admin");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }
      ProjectRole oldRole = member.getRole();
      member.setRole(ProjectRole.valueOf(updateProjectMemberRequest.getRole().toUpperCase()));
      projectMemberRepository.save(member);

      response.put("status", HttpStatus.OK.value());
      response.put("message", "Cập nhật vai trò thành công");
      return ResponseEntity.ok(response);
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
      ProjectMember projectMember = projectMemberRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("ProjectMember not found"));

      User user = projectMember.getUser();
      Project project = projectMember.getProject();

      // ✅ Không cho xóa nếu user là người tạo project
      if (project.getCreateBy().getId().equals(user.getId())) {
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Không thể xóa người tạo của project");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      // Tìm tất cả task trong project này mà user đang được gán
      List<Task> tasks = taskRepository.findByProjectAndUser(project, user);

      for (Task task : tasks) {
        task.setUser(null); // Gán user = null
      }

      taskRepository.saveAll(tasks); // Lưu lại các task đã chỉnh sửa

      // Xóa project member
      projectMemberRepository.deleteById(id);

      response.put("status", HttpStatus.OK.value());
      response.put("message", "Xóa project-member thành công và gỡ user khỏi các task liên quan");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  public ResponseEntity<Map<String, Object>> getAllMembers(Integer projectId) {
    Map<String, Object> response = new HashMap<>();
    try {
      Project project = projectRepository.findById(projectId).orElseThrow(
              () -> new RuntimeException("Project not found"));
      List<ProjectMember> projectMembers = projectMemberRepository.findByProject(project);
      List<Map<String, Object>> membersInfo = projectMembers.stream()
              .map(pm -> {
                Map<String, Object> memberInfo = new HashMap<>();
                memberInfo.put("id", pm.getId());
                memberInfo.put("userId", pm.getUser().getId());
                memberInfo.put("email", pm.getUser().getEmail());
                memberInfo.put("role", pm.getRole().toString());
                memberInfo.put("avatar", pm.getUser().getAvatar()); // Thêm thông tin avatar
                return memberInfo;
              })
              .collect(Collectors.toList());
      response.put("status", HttpStatus.OK.value());
      response.put("members", membersInfo);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  public ResponseEntity<Map<String, Object>> findByUserIdAndProjectId(Integer projectId, Integer userId) {
    Map<String, Object> response = new HashMap<>();
    try {
      ProjectMember projectMember = projectMemberRepository
              .findByProjectIdAndUserId(projectId, userId)
              .orElseThrow(() -> new RuntimeException("ProjectMember not found"));

      response.put("role", projectMember.getRole().toString()); // hoặc để luôn là Enum nếu bạn muốn
      response.put("status", HttpStatus.OK.value());

      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}
