package com.example.ui.Model;

public class Member {
    private Integer id; // Thêm id
    private Integer projectId;
    private String email;
    private String role;
    private String avatar;

    public Member(Integer projectId, String email, String role) {
        this.projectId = projectId;
        this.email = email;
        this.role = role;
    }

    public Member(String email, String role, String avatar) {
        this.email = email;
        this.role = role;
        this.avatar = avatar;
    }

    public Member(Integer id, String email, String role, String avatar) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.avatar = avatar;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getAvatar() {
        return avatar;
    }
}