package com.example.ui.Request;

public class UpdateUserRequest {
    private Integer id;
    private String name;
    private String avatar;

    public UpdateUserRequest(Integer id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }
}
