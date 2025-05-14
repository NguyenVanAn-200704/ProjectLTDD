package com.example.project.Controller;

import com.example.project.Request.EmailOTPRequest;
import com.example.project.Request.LoginRequest;
import com.example.project.Request.UpdateUserRequest;
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

  @PutMapping("/update")
  ResponseEntity<Map<String, Object>> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
    return userService.updateUser(updateUserRequest);
  }

  @PostMapping("/login")
  ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
    return userService.login(loginRequest);
  }

  @PostMapping("/send-otp")
  ResponseEntity<Map<String, Object>> sendOTP(@RequestParam String email) {
    return userService.sendOTP(email);
  }

  @PostMapping("/verify-otp")
  ResponseEntity<Map<String, Object>> verifyOTP(@Valid @RequestBody EmailOTPRequest emailOTPRequest) {
    return userService.verifyOTP(emailOTPRequest);
  }

  @PostMapping("/reset-password")
  ResponseEntity<Map<String, Object>> resetPassword(@Valid @RequestBody EmailOTPRequest emailOTPRequest) {
    return userService.resetPassword(emailOTPRequest);
  }

  @GetMapping("/task/all")
  ResponseEntity<Map<String, Object>> taskAll(@RequestParam Integer id) {
    return userService.allTasksInUser(id);
  }

  @GetMapping("/profile")
  ResponseEntity<Map<String, Object>> profile(@RequestParam Integer id) {
    return userService.profile(id);
  }

  @GetMapping("/check")
  ResponseEntity<Map<String, Object>> checkUser(@RequestParam String email) {
    return userService.checkUserByEmail(email);
  }
}