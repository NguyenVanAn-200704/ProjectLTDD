package com.example.project.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class VerifyOTPRequest {
  @NotBlank(message = "Email không được để trống !")
  @Email(message = "Email không hợp lệ !")
  private String email;

  @NotBlank(message = "OTP không được để trống !")
  @Size(min = 6, max = 6, message = "OTP không hợp lệ !")
  private String otp;
}
