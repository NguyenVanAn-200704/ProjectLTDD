package com.example.project.Entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serializable;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Column(unique = true, nullable = false)
  String email;

  @Column(nullable = false)
  String password;

  @Column(nullable = false)
  String name;

  String avatar;

  @OneToMany(mappedBy = "createBy", cascade = CascadeType.ALL)
  @JsonManagedReference
  List<Project> projects;

  @OneToMany(mappedBy = "user")
  @JsonManagedReference
  List<ProjectMember> projectMembers;

  @OneToMany(mappedBy = "user")
  @JsonManagedReference
  List<Task> tasks;
}
