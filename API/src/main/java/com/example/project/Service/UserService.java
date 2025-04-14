package com.example.project.Service;

import com.example.project.Entity.User;
import com.example.project.Mapper.UserMapper;
import com.example.project.Repository.UserRepository;
import com.example.project.Request.LoginRequest;
import com.example.project.Request.UserRequest;
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
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
}
