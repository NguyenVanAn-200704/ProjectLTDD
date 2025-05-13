package com.example.ui.Model;

public class Member {

    private int projectId;
    private String email;
    private String avatar;

    private String role;

    public Member(String email, String role, String avatar) {
        this.email = email;
        this.avatar = avatar;
        this.role = role;
    }

    public Member(int projectId, String email, String role) {
        this.projectId = projectId;
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
