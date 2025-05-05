package com.example.project.Entity;

import com.example.project.Enum.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Project implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Column(nullable = false)
  String name;

  String description;

  @ManyToOne
  @JoinColumn(name = "createBy", nullable = false)
  User createBy;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  ProjectStatus status;

  @Column(nullable = false)
  LocalDate createAt;

  @OneToMany(mappedBy = "project")
  List<ProjectMember> projectMembers;

  @OneToMany(mappedBy = "project")
  List<Task> tasks;

  @PrePersist
  protected void onCreate() {
    this.createAt = LocalDate.now();
    this.status = ProjectStatus.ACTIVE;
  }
}
