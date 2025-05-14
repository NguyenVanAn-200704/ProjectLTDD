package com.example.ui.Model;

import java.util.List;

public class User {
    private Integer id;
    private String email;
    private String password;
    private String name;
    private String avatar;
    private List<Project> projects;
    private List<ProjectMember> projectMembers;
    private List<TaskTemp> tasks;

    public User( String name, String email, Integer id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public User(String name){
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<ProjectMember> getProjectMembers() {
        return projectMembers;
    }

    public List<TaskTemp> getTasks() {
        return tasks;
    }
}
