package com.example.project.Entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    String avatar;

    @OneToMany(mappedBy = "createBy", cascade = CascadeType.ALL)
    List<Project> projects;

    @OneToMany(mappedBy = "user")
    List<ProjectMember> projectMembers;

    @OneToMany(mappedBy = "user")
    List<Task> tasks;
}
