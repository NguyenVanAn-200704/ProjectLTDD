package com.example.project.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailOTPRequest {
  private String email;

  private String password;

  private String otp;
}
