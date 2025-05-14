package com.example.project.Service;

import com.example.project.Entity.OTPToken;
import com.example.project.Entity.Task;
import com.example.project.Entity.User;
import com.example.project.Mapper.TaskMapper;
import com.example.project.Mapper.UpdateUserMapper;
import com.example.project.Mapper.UserMapper;
import com.example.project.Repository.OTPTokenRepository;
import com.example.project.Repository.TaskRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Request.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final TaskRepository taskRepository;
  private final OTPTokenRepository otpTokenRepository;
  private final UserMapper userMapper;
  private final UpdateUserMapper updateUserMapper;
  private final JavaMailSender javaMailSender;
  private final EmailService emailService;

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
  public ResponseEntity<Map<String, Object>> sendOTP(String email) {
    Map<String, Object> response = new HashMap<>();

    Optional<User> optionalUser = userRepository.findByEmail(email);
    if (optionalUser.isEmpty()) {
      response.put("status", HttpStatus.NOT_FOUND.value());
      response.put("message", "Email không tồn tại trong hệ thống!");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    try {
      String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));

      OTPToken otpToken = new OTPToken();
      otpToken.setEmail(email);
      otpToken.setOtp(otp);
      otpToken.setCreatedAt(LocalDateTime.now());
      otpToken.setExpiresAt(LocalDateTime.now().plusMinutes(10));

      otpTokenRepository.save(otpToken);

      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(email);
      message.setSubject("Khôi phục mật khẩu - Mã OTP");
      message.setText("Mã OTP của bạn là: " + otp + "\nOTP này sẽ hết hạn sau 10 phút.");
      javaMailSender.send(message);

      response.put("status", HttpStatus.OK.value());
      response.put("message", "Mã OTP đã được gửi đến email của bạn.");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi khi gửi OTP: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> verifyOTP(VerifyOTPRequest verifyOTPRequest) {
    Map<String, Object> response = new HashMap<>();

    try {
      Optional<OTPToken> otpTokenOptional = otpTokenRepository.findByEmailAndOtp(
        verifyOTPRequest.getEmail(), verifyOTPRequest.getOtp());

      if (otpTokenOptional.isEmpty()) {
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "OTP không hợp lệ!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      OTPToken otpToken = otpTokenOptional.get();
      if (otpToken.getExpiresAt().isBefore(LocalDateTime.now())) {
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "OTP đã hết hạn!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      response.put("status", HttpStatus.OK.value());
      response.put("message", "Xác minh OTP thành công!");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi khi xác minh OTP: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @Transactional
  public ResponseEntity<Map<String, Object>> resetPassword(ResetPasswordRequest resetPasswordRequest) {
    Map<String, Object> response = new HashMap<>();

    try {
      Optional<User> optionalUser = userRepository.findByEmail(resetPasswordRequest.getEmail());
      if (optionalUser.isEmpty()) {
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", "Người dùng không tồn tại!");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      User user = optionalUser.get();
      String newPassword = resetPasswordRequest.getPassword();
      if (newPassword == null || newPassword.isBlank()) {
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Mật khẩu mới không được để trống!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      user.setPassword(newPassword);
      userRepository.save(user);

      String subject = "Mật khẩu mới của bạn";
      String body = "Mật khẩu của bạn đã được đặt lại thành công. Vui lòng đăng nhập với mật khẩu mới.";
      emailService.sendEmail(user.getEmail(), subject, body);

      otpTokenRepository.deleteByEmail(resetPasswordRequest.getEmail());

      response.put("status", HttpStatus.OK.value());
      response.put("message", "Đặt lại mật khẩu thành công!");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.put("message", "Lỗi khi đặt lại mật khẩu: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
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
      data.put("avatar", user.getAvatar());

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