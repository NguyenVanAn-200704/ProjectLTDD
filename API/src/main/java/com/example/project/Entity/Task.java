package com.example.project.Entity;

import com.example.project.Enum.TaskPriority;
import com.example.project.Enum.TaskStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Task implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Column(nullable = false)
  String title;

  String description;

  @Enumerated(EnumType.STRING)
  TaskStatus status;

  @Enumerated(EnumType.STRING)
  TaskPriority priority;

  @ManyToOne
  @JoinColumn(name = "projectId", nullable = false)
  @JsonBackReference
  Project project;

  @ManyToOne
  @JoinColumn(name = "assignId")
  @JsonManagedReference
  User user;

  @Column(nullable = false)
  LocalDate dueDate;

  LocalDate createdDate;

  @Column
  private String fileUrl;

  @PrePersist
  protected void onCreate() {
    this.status = TaskStatus.TO_DO;
    this.createdDate = LocalDate.now();
  }
}
