package com.example.ui.Request;


import java.time.LocalDate;

public class UpdateTaskRequest {
    private Integer id;
    private String title;

    private String description;

    private String priority;

    private String status;

    private String email;

    private String fileUrl;

    private String dueDate;

    public UpdateTaskRequest(Integer Id,String title, String description,String status, String priority, String email, String fileUrl, String dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.id = Id;
        this.status =status;
        this.email = email;
        this.fileUrl = fileUrl;
        this.dueDate = dueDate;
    }
}
