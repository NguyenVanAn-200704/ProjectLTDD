package com.example.project.Service;

import com.example.project.Entity.Project;
import com.example.project.Entity.Task;
import com.example.project.Entity.User;
import com.example.project.Enum.TaskPriority;
import com.example.project.Enum.TaskStatus;
import com.example.project.Mapper.TaskMapper;
import com.example.project.Mapper.UpdateTaskMapper;
import com.example.project.Repository.ProjectMemberRepository;
import com.example.project.Repository.ProjectRepository;
import com.example.project.Repository.TaskRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Request.TaskRequest;
import com.example.project.Request.UpdateTaskRequest;
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
public class TaskService {
  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final ProjectMemberRepository projectMemberRepository;
  private final TaskMapper taskMapper;
  private final UpdateTaskMapper updateTaskMapper;

  @Transactional
  public ResponseEntity<Map<String, Object>> addTask(TaskRequest taskRequest) {
    Map<String, Object> response = new HashMap<>();
    try {
      Project project = projectRepository.findById(taskRequest.getProjectId())
        .orElseThrow(() -> new RuntimeException("Project not found !"));

      // Kiểm tra tiêu đề trùng
      boolean existsTaskWithSameTitle = taskRepository.existsByProjectAndTitle(project, taskRequest.getTitle());
      if (existsTaskWithSameTitle) {
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Title này đã tồn tại trong Project !");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }

      Task task = taskMapper.taskRequestToTask(taskRequest);
      task.setProject(project);
      // Nếu userId không null thì xử lý tiếp
      if (taskRequest.getEmail() != null) {
        User user = userRepository.findByEmail(taskRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found !"));

        boolean isProjectMember = projectMemberRepository
          .existsByProjectIdAndUserId(project.getId(), user.getId());
        if (!isProjectMember) {
          response.put("status", HttpStatus.BAD_REQUEST.value());
          response.put("message", "Người dùng không phải là thành viên của Project !");
          return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        task.setUser(user); // Gán user nếu hợp lệ
      } else {
        task.setUser(null); // Gán null nếu không có user
      }

      taskRepository.save(task);

      response.put("status", HttpStatus.CREATED.value());
      response.put("message", "Tạo task thành công");
      return new ResponseEntity<>(response, HttpStatus.CREATED);

    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi add task: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
  @Transactional
  public ResponseEntity<Map<String, Object>> updateTask(UpdateTaskRequest updateTaskRequest) {
    Map<String, Object> response = new HashMap<>();
    try {

      Task task = taskRepository.findById(updateTaskRequest.getId())
              .orElseThrow(() -> new RuntimeException("Task not found: " + updateTaskRequest.getId()));

      // Ánh xạ thủ công các trường từ UpdateTaskRequest sang Task
      task.setTitle(updateTaskRequest.getTitle());
      task.setDescription(updateTaskRequest.getDescription());
      if (updateTaskRequest.getStatus() != null) {
        task.setStatus(TaskStatus.valueOf(updateTaskRequest.getStatus()));
      }
      if (updateTaskRequest.getPriority() != null) {
        task.setPriority(TaskPriority.valueOf(updateTaskRequest.getPriority()));
      }
      task.setDueDate(updateTaskRequest.getDueDate());
      task.setFileUrl(updateTaskRequest.getFileUrl());

      // Cập nhật user
      if (updateTaskRequest.getEmail() != null && !updateTaskRequest.getEmail().isEmpty()) {
        User user = userRepository.findByEmail(updateTaskRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + updateTaskRequest.getEmail()));
        task.setUser(user);
      } else {
        task.setUser(null); // Cho phép bỏ gán user
      }

      taskRepository.save(task);

      response.put("status", HttpStatus.OK.value());
      response.put("message", "Cập nhật task thành công");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi update task: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }


  @Transactional
  public ResponseEntity<Map<String, Object>> deleteTask(Integer id) {
    Map<String, Object> response = new HashMap<>();

    try {
      taskRepository.deleteById(id);
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Xóa task thành công");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi delete task: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
  @Transactional
  public ResponseEntity<Map<String, Object>> findById(Integer id) {
    Map<String, Object> response = new HashMap<>();

    try {
      Task task = taskRepository.findById(id).orElseThrow(
              () -> new RuntimeException("Task not found ! " + id));
      Map<String, Object> taskInfo = new HashMap<>();
      taskInfo.put("id", task.getId());
      taskInfo.put("title", task.getTitle());
      taskInfo.put("description", task.getDescription());
      taskInfo.put("status", task.getStatus().toString());
      taskInfo.put("priority", task.getPriority().toString());
      taskInfo.put("dueDate", task.getDueDate().toString());
      taskInfo.put("createdDate", task.getCreatedDate().toString()); // Thêm createdDate
      taskInfo.put("fileUrl", task.getFileUrl());
      taskInfo.put("projectId", task.getProject().getId());

      if (task.getUser() != null) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", task.getUser().getId());
        userInfo.put("name", task.getUser().getName());
        userInfo.put("email", task.getUser().getEmail());
        taskInfo.put("user", userInfo);
      } else {
        taskInfo.put("user", null);
      }

      response.put("task", taskInfo);
      response.put("status", HttpStatus.OK.value());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi khi lấy chi tiết task: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}
