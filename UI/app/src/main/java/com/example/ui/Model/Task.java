package com.example.ui.Model;

import java.time.LocalDate;

public class Task {
    private Integer id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private User user;
    private LocalDate dueDate;

    private String fileUrl;
    private String createdDate;

    public Task(Integer id,LocalDate dueDate, User user,  String status,  String title) {
        this.dueDate = dueDate;
        this.user = user;
        this.status = status;
        this.title = title;
        this.id = id;
    }
    public Task(int id, LocalDate dueDate, User assignedUser, String status, String title, String priority) {
        this.id = id;
        this.dueDate = dueDate;
        this.user = assignedUser;
        this.status = status;
        this.title = title;
        this.priority = priority;
    }

    public Task(Integer id, String title, String description, String status, String priority, User user, LocalDate dueDate, String fileUrl, String createdDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.user = user;
        this.dueDate = dueDate;
        this.fileUrl = fileUrl;
        this.createdDate = createdDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
