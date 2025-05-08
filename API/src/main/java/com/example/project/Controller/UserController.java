package com.example.project.Controller;

import com.example.project.Request.LoginRequest;
import com.example.project.Request.UserRequest;
import com.example.project.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PostMapping("/create")
  ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserRequest userRequest) {
    return userService.createUser(userRequest);
  }

  @PostMapping("/login")
  ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
    return userService.login(loginRequest);
  }

  @GetMapping("/task/all")
  ResponseEntity<Map<String, Object>> taskAll(@RequestParam Integer id) {
    return userService.allTasksInUser(id);
  }
}
