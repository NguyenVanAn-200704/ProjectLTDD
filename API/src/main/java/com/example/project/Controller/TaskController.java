package com.example.project.Controller;

import com.example.project.Request.TaskRequest;
import com.example.project.Request.UpdateTaskRequest;
import com.example.project.Service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/project/task")
@RequiredArgsConstructor
public class TaskController {
  private final TaskService taskService;

  @PostMapping("/add")
  ResponseEntity<Map<String, Object>> addTask(@Valid @RequestBody TaskRequest taskRequest) {
    return taskService.addTask(taskRequest);
  }

  @PutMapping("/update")
  ResponseEntity<Map<String, Object>> updateTask(@Valid @RequestBody UpdateTaskRequest updateTaskRequest) {
    return taskService.updateTask(updateTaskRequest);
  }

  @DeleteMapping("/delete")
  ResponseEntity<Map<String, Object>> deleteTask(@RequestParam Integer id) {
    return taskService.deleteTask(id);
  }

  @GetMapping("/get")
  ResponseEntity<Map<String, Object>> getTask(@RequestParam Integer id) { return taskService.findById(id);
  }
}
