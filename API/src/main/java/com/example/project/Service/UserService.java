package com.example.project.Service;

import com.example.project.Entity.Task;
import com.example.project.Entity.User;
import com.example.project.Mapper.UpdateUserMapper;
import com.example.project.Mapper.UserMapper;
import com.example.project.Repository.TaskRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Request.LoginRequest;
import com.example.project.Request.UpdateUserRequest;
import com.example.project.Request.UserRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final TaskRepository taskRepository;
  private final UserMapper userMapper;
  private final UpdateUserMapper updateUserMapper;

  @Transactional
  public ResponseEntity<Map<String, Object>> createUser(UserRequest userRequest) {
    Map<String, Object> response = new HashMap<>();

    if (userRepository.existsByEmail(userRequest.getEmail())) {
      response.put("status", HttpStatus.BAD_REQUEST.value());
      response.put("message", "Email đã tồn tại");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    try {
      User user = userMapper.userRequestToUser(userRequest);
      userRepository.save(user);
      response.put("status", HttpStatus.CREATED.value());
      response.put("message", "Tạo tài khoản thành công");
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> updateUser(UpdateUserRequest updateUserRequest) {
    Map<String, Object> response = new HashMap<>();

    try {
      Optional<User> optionalUser = userRepository.findById(updateUserRequest.getId());
      if (optionalUser.isEmpty()) {
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", "Người dùng không tồn tại");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      User user = optionalUser.get();
      updateUserMapper.updateUserFromProfile(updateUserRequest, user);
      userRepository.save(user);

      response.put("status", HttpStatus.OK.value());
      response.put("message", "Cập nhật hồ sơ thành công");
      response.put("user", user);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi khi cập nhật hồ sơ: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> login(LoginRequest loginRequest) {
    Map<String, Object> response = new HashMap<>();
    Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

    if (optionalUser.isEmpty()) {
      response.put("status", HttpStatus.BAD_REQUEST.value());
      response.put("message", "Email không tồn tại !");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    User user = optionalUser.get();

    if (!user.getPassword().equals(loginRequest.getPassword())) {
      response.put("status", HttpStatus.BAD_REQUEST.value());
      response.put("message", "Mật khẩu không đúng !");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    response.put("status", HttpStatus.OK.value());
    response.put("message", "Đăng nhập thành công");
    response.put("user", user);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> allTasksInUser(Integer userId) {
    Map<String, Object> response = new HashMap<>();

    try {
      User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found!"));
      List<Task> tasks = taskRepository.findByUser(user);
      response.put("status", HttpStatus.OK.value());
      response.put("tasks", tasks);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> profile(Integer userId) {
    Map<String, Object> response = new HashMap<>();

    try {
      User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found!"));
      response.put("status", HttpStatus.OK.value());
      response.put("user", user);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi profileUser: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> checkUserByEmail(String email) {
    Map<String, Object> response = new HashMap<>();

    try {
      User user = userRepository.findByEmail(email)
              .orElseThrow(() -> new RuntimeException("User not found!"));

      Map<String, Object> data = new HashMap<>();
      data.put("email", user.getEmail());
      data.put("avatar", user.getAvatar()); // Giả sử User có phương thức getAvatar()

      response.put("status", HttpStatus.OK.value());
      response.put("message", "User found");
      response.put("data", data);

      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi profileUser: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

}
