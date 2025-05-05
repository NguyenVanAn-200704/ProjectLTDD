package com.example.project.Entity;

import com.example.project.Enum.ProjectRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"projectId", "userId"})
  }
)
public class ProjectMember implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne
  @JoinColumn(name = "projectId")
  @JsonBackReference
  Project project;

  @ManyToOne
  @JoinColumn(name = "userId")
  @JsonBackReference
  User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  ProjectRole role;

  @Column(nullable = false)
  LocalDate joinDate;

  @PrePersist
  protected void onCreate() {
    this.joinDate = LocalDate.now();
  }
}
