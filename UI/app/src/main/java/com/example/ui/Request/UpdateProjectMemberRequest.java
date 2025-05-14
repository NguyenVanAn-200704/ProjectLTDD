package com.example.ui.Request;

public class UpdateProjectMemberRequest {
    private Integer id;

    private String role;

    private Integer currentUserId;

    public UpdateProjectMemberRequest(Integer id, String role, Integer currentUserId) {
        this.id = id;
        this.role = role;
        this.currentUserId = currentUserId;
    }
}
