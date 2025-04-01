package com.example.project.Entity;

import com.example.project.Enum.ProjectRole;
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
public class ProjectMember implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "projectId")
    Project project;

    @ManyToOne
    @JoinColumn(name = "userId")
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
