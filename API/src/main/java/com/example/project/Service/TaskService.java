package com.example.project.Service;

import com.example.project.Entity.Project;
import com.example.project.Entity.Task;
import com.example.project.Entity.User;
import com.example.project.Mapper.TaskMapper;
import com.example.project.Mapper.UpdateTaskMapper;
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

@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final TaskMapper taskMapper;
  private final UpdateTaskMapper updateTaskMapper;

  @Transactional
  public ResponseEntity<Map<String, Object>> addTask(TaskRequest taskRequest) {
    Map<String, Object> response = new HashMap<>();
    try {
      Project project = projectRepository.findById(taskRequest.getProjectId())
        .orElseThrow(() -> new RuntimeException("Project not found !"));

      User user = userRepository.findById(taskRequest.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found !"));

      boolean existsTaskWithSameTitle = taskRepository.existsByProjectAndTitle(project, taskRequest.getTitle());
      if (existsTaskWithSameTitle) {
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Title này đã tồn tại trong Project !");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }

      Task task = taskMapper.taskRequestToTask(taskRequest);
      task.setProject(project);
      task.setUser(user);
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
      Task task = taskRepository.findById(updateTaskRequest.getId()).orElseThrow(
        () -> new RuntimeException("Task not found ! " + updateTaskRequest.getId()));
      updateTaskMapper.updateTaskFromRequest(updateTaskRequest, task);
      User user = userRepository.findById(updateTaskRequest.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found!"));
      task.setUser(user);
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
}
