package com.example.project.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailOTPRequest {
  @NotBlank(message = "Email không được để trống !")
  @Email(message = "Email không hợp lệ !")
  private String email;

  @NotBlank(message = "Password không được để trống !")
  private String password;

  @NotBlank(message = "OTP không được để trống !")
  private String otp;
}
