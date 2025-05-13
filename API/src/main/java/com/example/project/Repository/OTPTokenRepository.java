package com.example.project.Repository;

import com.example.project.Entity.OTPToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPTokenRepository extends JpaRepository<OTPToken, Integer> {
  Optional<OTPToken> findByEmailAndOtp(String email, String otp);
}
