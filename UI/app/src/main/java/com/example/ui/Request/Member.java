package com.example.ui.Request;

public class Member {
    private String email;
    private String avatar;

    public Member(String email, String avatar) {
        this.email = email;
        this.avatar = avatar;
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
}
