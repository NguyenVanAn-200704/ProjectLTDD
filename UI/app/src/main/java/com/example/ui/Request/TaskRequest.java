package com.example.ui.Request;


import java.time.LocalDate;

public class TaskRequest {
    private String title;

    private String description;

    private String priority;

    private Integer projectId;

    private String email;

    private String fileUrl;

    private String dueDate;

    public TaskRequest(String title, String description, String priority, Integer projectId, String email, String fileUrl, String dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.projectId = projectId;
        this.email = email;
        this.fileUrl = fileUrl;
        this.dueDate = dueDate;
    }
}
