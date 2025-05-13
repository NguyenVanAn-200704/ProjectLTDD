package com.example.project.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class OTPToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String email;
  private String otp;
  private LocalDateTime createdAt;
  private LocalDateTime expiresAt;
}
