package com.example.project.Entity;

import com.example.project.Enum.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
  @JsonBackReference
  User createBy;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  ProjectStatus status;

  @Column(nullable = false)
  LocalDate createAt;

  @OneToMany(mappedBy = "project")
  @JsonIgnore
  List<ProjectMember> projectMembers;

  @OneToMany(mappedBy = "project")
  @JsonIgnore
  List<Task> tasks;

  @PrePersist
  protected void onCreate() {
    this.createAt = LocalDate.now();
    this.status = ProjectStatus.ACTIVE;
  }
}
