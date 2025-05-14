package com.example.project.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ResetPasswordRequest {
  @NotBlank(message = "Email không được để trống !")
  @Email(message = "Email không hợp lệ !")
  private String email;

  @NotBlank(message = "Password không được để trống !")
  @Size(min = 5, max = 25, message = "Password từ 5-25 ký tự !")
  private String password;
}
