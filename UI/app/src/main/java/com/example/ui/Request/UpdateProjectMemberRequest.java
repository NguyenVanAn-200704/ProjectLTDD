package com.example.ui.Request;

public class UpdateProjectMemberRequest {
    private Integer id;

    private String role;

    public UpdateProjectMemberRequest(Integer id, String role) {
        this.id = id;
        this.role = role;
    }
}
