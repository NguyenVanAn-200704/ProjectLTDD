package com.example.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender javaMailSender;

  public void sendEmail(String to, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("luuxuandung2004@gmail.com"); // Email gửi đi
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);
    javaMailSender.send(message);
  }
}
