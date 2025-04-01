package com.example.project.Entity;

import com.example.project.Enum.TaskPriority;
import com.example.project.Enum.TaskStatus;
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
    Project project;

    @ManyToOne
    @JoinColumn(name = "assignId")
    User user;

    @Column(nullable = false)
    LocalDate dueDate;

    LocalDate createdDate;

    @PrePersist
    protected void onCreate() {
        this.status = TaskStatus.TO_DO;
        this.createdDate = LocalDate.now();
    }
}
