package com.example.ui.Model;

import com.example.ui.Enum.ProjectRole;

import java.time.LocalDate;

public class ProjectMember {
    private Integer id;

    private Project project;
    private User user;
    private ProjectRole role;
    private LocalDate joinDate;

}
