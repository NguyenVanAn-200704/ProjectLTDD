package com.example.ui.Model;

import java.time.LocalDate;

public class TaskTemp {
    private int id;
    private String title;

    private int projectId;
    private LocalDate dueDate;
    private String status;

    private String role;
    private String priority;

    public TaskTemp(int id, String title, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.status = null; // Hoặc giá trị mặc định khác nếu cần
        this.priority = null; // Hoặc giá trị mặc định khác nếu cần
    }

    public TaskTemp(Integer id, Integer projectId, String title, LocalDate dueDate, String status, String priority, String role) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
        this.role = role;
    }

    public String getRole(){
        return role;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public int getProjectId(){
        return projectId;
    }

    // Các setter nếu cần
}