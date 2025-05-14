package com.example.project.Service;

import com.example.project.Entity.Project;
import com.example.project.Entity.Task;
import com.example.project.Entity.User;
import com.example.project.Enum.TaskPriority;
import com.example.project.Enum.TaskStatus;
import com.example.project.Mapper.TaskMapper;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final ProjectMemberRepository projectMemberRepository;
  private final TaskMapper taskMapper;
  private final JavaMailSender javaMailSender;
  private final EmailService emailService;

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
      User assignedUser = null;

      // Nếu userId không null thì xử lý tiếp
      if (taskRequest.getEmail() != null && !taskRequest.getEmail().isEmpty()) {
        User user = userRepository.findByEmail(taskRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found !"));

        boolean isProjectMember = projectMemberRepository
                .existsByProjectIdAndUserId(project.getId(), user.getId());
        if (!isProjectMember) {
          response.put("status", HttpStatus.BAD_REQUEST.value());
          response.put("message", "Người dùng không phải là thành viên của Project !");
          return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        task.setUser(user);
        assignedUser = user;
      } else {
        task.setUser(null);
      }

      taskRepository.save(task);

      // Gửi email thông báo nếu có người được giao task
      if (assignedUser != null) {
        String subject = "Bạn được giao một Task mới";
        String body = String.format(
                "Chào %s,\n\nBạn vừa được giao một task mới trong dự án %s:\n" +
                        "- Tiêu đề: %s\n" +
                        "- Mô tả: %s\n" +
                        "- Hạn chót: %s\n" +
                        "- Ưu tiên: %s\n" +
                        "- Trạng thái: %s\n\n" +
                        "Vui lòng kiểm tra và bắt đầu làm việc.\n" +
                        "Trân trọng,\nHệ thống quản lý dự án",
                assignedUser.getName() != null ? assignedUser.getName() : assignedUser.getEmail(),
                project.getName(),
                task.getTitle(),
                task.getDescription() != null ? task.getDescription() : "Không có mô tả",
                task.getDueDate() != null ? task.getDueDate().toString() : "Không có hạn chót",
                task.getPriority() != null ? task.getPriority().toString() : "Không xác định",
                task.getStatus() != null ? task.getStatus().toString() : "Không xác định"
        );
        emailService.sendEmail(assignedUser.getEmail(), subject, body);
      }

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

      User previousUser = task.getUser(); // Lưu người được giao trước đó
      String previousTitleere;

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
      User newUser = null;
      if (updateTaskRequest.getEmail() != null && !updateTaskRequest.getEmail().isEmpty()) {
        newUser = userRepository.findByEmail(updateTaskRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + updateTaskRequest.getEmail()));
        task.setUser(newUser);
      } else {
        task.setUser(null);
      }

      taskRepository.save(task);

      // Gửi email thông báo
      if (newUser != null) {
        // Trường hợp: Task được giao cho người mới hoặc lần đầu được giao
        if (previousUser == null || !previousUser.getId().equals(newUser.getId())) {
          String subject = "Bạn được giao một Task mới";
          String body = String.format(
                  "Chào %s,\n\nBạn vừa được giao một task trong dự án %s:\n" +
                          "- Tiêu đề: %s\n" +
                          "- Mô tả: %s\n" +
                          "- Hạn chót: %s\n" +
                          "- Ưu tiên: %s\n" +
                          "- Trạng thái: %s\n\n" +
                          "Vui lòng kiểm tra và bắt đầu làm việc.\n" +
                          "Trân trọng,\nHệ thống quản lý dự án",
                  newUser.getName() != null ? newUser.getName() : newUser.getEmail(),
                  task.getProject().getName(),
                  task.getTitle(),
                  task.getDescription() != null ? task.getDescription() : "Không có mô tả",
                  task.getDueDate() != null ? task.getDueDate().toString() : "Không có hạn chót",
                  task.getPriority() != null ? task.getPriority().toString() : "Không xác định",
                  task.getStatus() != null ? task.getStatus().toString() : "Không xác định"
          );
          emailService.sendEmail(newUser.getEmail(), subject, body);
        } else {
          // Trường hợp: Task đã có người được giao và được cập nhật
          String subject = "Task của bạn đã được cập nhật";
          String body = String.format(
                  "Chào %s,\n\nTask của bạn trong dự án %s đã được cập nhật:\n" +
                          "- Tiêu đề: %s\n" +
                          "- Mô tả: %s\n" +
                          "- Hạn chót: %s\n" +
                          "- Ưu tiên: %s\n" +
                          "- Trạng thái: %s\n\n" +
                          "Vui lòng kiểm tra các thay đổi.\n" +
                          "Trân trọng,\nHệ thống quản lý dự án",
                  newUser.getName() != null ? newUser.getName() : newUser.getEmail(),
                  task.getProject().getName(),
                  task.getTitle(),
                  task.getDescription() != null ? task.getDescription() : "Không có mô tả",
                  task.getDueDate() != null ? task.getDueDate().toString() : "Không có hạn chót",
                  task.getPriority() != null ? task.getPriority().toString() : "Không xác định",
                  task.getStatus() != null ? task.getStatus().toString() : "Không xác định"
          );
          emailService.sendEmail(newUser.getEmail(), subject, body);
        }
      }

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